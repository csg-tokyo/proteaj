package proteaj.pparser;

import java.util.*;
import javassist.*;

import proteaj.error.*;
import proteaj.util.*;

import static java.lang.Character.*;
import static proteaj.pparser.PackratParserCombinators.*;

public class CommonParsers {
  private static Map<String, PackratParser<String>> elementParsers = new HashMap<>();
  private static Map<String, PackratParser<String>> keywordParsers = new HashMap<>();

  public static final PackratParser<?> whitespaces =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(PackratReader reader, Environment env) {
          while(isWhitespace(reader.lookahead())) reader.next();
          return dummyResult;
        }
      };

  public static PackratParser<String> element (final String word) {
    if (! elementParsers.containsKey(word)) elementParsers.put(word, makeElementParser(word));
    return elementParsers.get(word);
  }

  public static PackratParser<String> keyword (final String word) {
    if (! keywordParsers.containsKey(word)) keywordParsers.put(word, PackratParserCombinators.prefix(whitespaces, element(word)));
    return keywordParsers.get(word);
  }

  public static PackratParser<String[]> keywords (final String... words) {
    return new PackratParser<String[]>() {
      @Override
      protected ParseResult<String[]> parse(PackratReader reader, Environment env) {
        final int pos = reader.getPos();

        for (String word : words) {
          ParseResult<String> result = keyword(word).applyRule(reader, env);
          if (result.isFail()) return fail(result, pos, reader);
        }

        return success(words);
      }
    };
  }

  public static <T> PackratParser<T> prefix (String prefix, PackratParser<T> parser) {
    return PackratParserCombinators.prefix(keyword(prefix), parser);
  }

  public static <T1, T2> PackratParser<Pair<T1, T2>> prefix (String prefix, PackratParser<T1> parser1, String infix, PackratParser<T2> parser2) {
    return PackratParserCombinators.prefix(keyword(prefix), parser1, keyword(infix), parser2);
  }

  public static <T> PackratParser<T> postfix (PackratParser<T> parser, String postfix) {
    return PackratParserCombinators.postfix(parser, keyword(postfix));
  }

  public static <T> PackratParser<T> enclosed (String prefix, PackratParser<T> parser, String postfix) {
    return PackratParserCombinators.enclosed(keyword(prefix), parser, keyword(postfix));
  }

  public static <T1, T2> PackratParser<Pair<T1, T2>> infix (PackratParser<T1> parser1, String infix, PackratParser<T2> parser2) {
    return PackratParserCombinators.infix(parser1, keyword(infix), parser2);
  }

  public static <T> PackratParser<List<T>> rep (PackratParser<T> parser, String sep) {
    return PackratParserCombinators.rep(parser, keyword(sep));
  }

  public static <T> PackratParser<List<T>> rep1 (PackratParser<T> parser, String sep) {
    return PackratParserCombinators.rep1(parser, keyword(sep));
  }

  public static <T> PackratParser<List<T>> sequence (List<PackratParser<T>> parsers, String sep) {
    return PackratParserCombinators.sequence(parsers, keyword(sep));
  }

  public static final PackratParser<Character> letter =
      new PackratParser<Character>() {
        @Override
        protected ParseResult<Character> parse(PackratReader reader, Environment env) {
          if (isLetter(reader.lookahead())) return success(reader.next());
          else return fail("expected letter, but found " + (char)reader.lookahead(), reader.getPos(), reader);
        }
      };

  public static final PackratParser<String> identifier =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(PackratReader reader, Environment env) {
          int pos = reader.getPos();

          while(isWhitespace(reader.lookahead())) reader.next();

          if(! isJavaIdentifierStart(reader.lookahead())) {
            return fail("expected identifier, but found " + (char)reader.lookahead(), pos, reader);
          }

          StringBuilder buf = new StringBuilder();
          buf.append(reader.next());

          while(reader.hasNext() && isJavaIdentifierPart(reader.lookahead())) {
            buf.append(reader.next());
          }

          return success(buf.toString());
        }
      };

  public static final PackratParser<Long> integer =
      new PackratParser<Long>() {
        @Override
        protected ParseResult<Long> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          while(isWhitespace(reader.lookahead())) reader.next();

          if (isDigit(reader.lookahead())) {
            if(reader.lookahead() == 0) {
              reader.next();
              return success(0L);
            }

            long val = digit(reader.next(), 10);

            while(isDigit(reader.lookahead())) {
              val = val * 10 + digit(reader.next(), 10);
            }

            return success(val);
          }

          return fail("digit is not found", pos, reader);
        }
      };

  public static final PackratParser<Double> decimal =
      new PackratParser<Double>() {
        @Override
        protected ParseResult<Double> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          ParseResult<Long> i = integer.applyRule(reader, env);
          if (i.isFail()) return fail(i, pos, reader);

          StringBuilder buf = new StringBuilder();
          buf.append(i.get());

          ParseResult<String> dot = element(".").applyRule(reader, env);
          if (dot.isFail()) return fail(dot, pos, reader);

          buf.append('.');

          while(isDigit(reader.lookahead())) {
            buf.append(reader.next());
          }

          return success(Double.valueOf(buf.toString()));
        }
      };

  public static final PackratParser<Float> floatConst =
      map(PackratParserCombinators.postfix(decimal, element("f")), v -> v.floatValue());

  public static final PackratParser<Long> hexadecimal =
      new PackratParser<Long>() {
        @Override
        protected ParseResult<Long> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          // 0x
          ParseResult<String> bdq = keyword("0x").applyRule(reader, env);
          if(bdq.isFail()) return fail(bdq, pos, reader);

          if (! reader.hasNext()) return fail("digit is not found", pos, reader);

          long val = digit(reader.next(), 16);
          if (val == -1) return fail("invalid hexadecimal", pos, reader);

          while (reader.hasNext()) {
            int d = digit(reader.lookahead(), 16);
            if (d == -1) break;

            reader.next();
            val = val * 16 + d;
          }

          return success(val);
        }
      };

  public static final PackratParser<String> string =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          // '"'
          ParseResult<String> bdq = keyword("\"").applyRule(reader, env);
          if(bdq.isFail()) return fail(bdq, pos, reader);

          StringBuilder buf = new StringBuilder();

          while(reader.hasNext() && reader.lookahead() != '\n') {
            int ch = reader.next();
            switch(ch) {
              case '"' : return success(buf.toString());
              case '\\':
                ch = readEscapedChar(reader);
                if(ch == -1) return fail("invalid escape character", pos, reader);
              default: buf.append((char)ch);
            }
          }

          return fail("unterminated string literal", pos, reader);
        }
      };

  public static final PackratParser<Character> character =
      new PackratParser<Character>() {
        @Override
        protected ParseResult<Character> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          // '\''
          ParseResult<String> bquote = keyword("\'").applyRule(reader, env);
          if(bquote.isFail()) return fail(bquote, pos, reader);

          if(reader.hasNext() && reader.lookahead() != '\n') {
            int ch = reader.next();

            if(ch == '\'') return fail("invalid character literal", pos, reader);
            else if(ch == '\\') {
              ch = readEscapedChar(reader);
              if(ch == -1) return fail("invalid escape character", pos, reader);
            }

            if(reader.lookahead() == '\'') {
              reader.next();
              return success((char)ch);
            }
          }

          return fail("unterminated character literal", pos, reader);
        }
      };

  public static final PackratParser<String> qualifiedIdentifier =
      map(rep1(identifier, "."), strings -> {
        StringBuilder buf = new StringBuilder(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
          buf.append('.').append(strings.get(i));
        }
        return buf.toString();
      });

  public static final PackratParser<Integer> arrayBrackets =
      map(PackratParserCombinators.rep(keywords("[", "]")), pairs -> pairs.size());

  public static final PackratParser<CtClass> className =
      new PackratParser<CtClass>() {
        @Override
        protected ParseResult<CtClass> parse(PackratReader reader, Environment env) {
          final int pos = reader.getPos();

          ParseResult<String> id0 = identifier.applyRule(reader, env);
          if (id0.isFail()) return fail(id0, pos, reader);

          PackratParser<String> dotParser = keyword(".");
          String name = id0.get();
          int dPos;

          while (true) {
            dPos = reader.getPos();

            if (dotParser.applyRule(reader, env).isFail()) break;

            ParseResult<String> id = identifier.applyRule(reader, env);
            if (id.isFail()) break;

            String lName = name + '.' + id.get();
            if (env.isTypeName(name) && (! env.isTypeName(lName))) break;
            else name = lName;
          }

          reader.setPos(dPos);

          try { return success(env.getType(name)); }
          catch (NotFoundError e) { return fail(e.getMessage(), pos, reader); }
        }
      };

  public static final PackratParser<CtClass> typeName =
      bind(seq(qualifiedIdentifier, arrayBrackets), pair -> depends(env -> {
        try { return unit(env.getArrayType(pair._1, pair._2)); }
        catch (NotFoundError e) { return failure(e.getMessage()); }
      }));

  public static final PackratParser<String> untilWhitespace =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(PackratReader reader, Environment env) {
          StringBuilder buf = new StringBuilder();

          while(reader.hasNext()) {
            if(isWhitespace(reader.lookahead())) break;
            buf.append(reader.next());
          }

          return success(buf.toString());
        }
      };

  private static int readEscapedChar(PackratReader reader) {
    char ch = reader.next();

    if(escapedChars.containsKey(ch)) return escapedChars.get(ch);
    else return -1;
  }

  private static final Map<Character, Character> escapedChars = new HashMap<>();

  static {
    escapedChars.put('b', '\b');
    escapedChars.put('t', '\t');
    escapedChars.put('n', '\n');
    escapedChars.put('f', '\f');
    escapedChars.put('r', '\r');
    escapedChars.put('\'', '\'');
    escapedChars.put('\"', '\"');
    escapedChars.put('\\', '\\');
  }

  private static PackratParser<String> makeElementParser (final String word) {
    assert word != null;
    if (word.isEmpty()) return unit(word);
    else return new PackratParser<String>() {
      @Override
      protected ParseResult<String> parse(PackratReader reader, Environment env) {
        final int pos = reader.getPos();
        final int len = word.length();

        for (int i = 0; i < len; i++) {
          if (reader.lookahead() == word.charAt(i)) reader.next();
          else return fail("expected token \"" + word + "\" is not found", pos, reader);
        }

        return success(word);
      }
    };
  }

  private static final ParseResult<String> dummyResult = new Success<>("");
}
