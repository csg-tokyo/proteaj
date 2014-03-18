package proteaj.pparser;

import java.util.*;
import javassist.*;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.util.*;

import static java.lang.Character.*;
import static proteaj.pparser.PackratParserCombinators.*;

public class CommonParsers {
  public static final PackratParser<String> identifier =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
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

  public static final PackratParser<Integer> integer =
      new PackratParser<Integer>() {
        @Override
        protected ParseResult<Integer> parse(SourceStringReader reader, Environment env) {
          final int pos = reader.getPos();

          while(isWhitespace(reader.lookahead())) reader.next();

          if(isDigit(reader.lookahead())) {
            if(reader.lookahead() == 0) {
              reader.next();
              return success(0);
            }

            int val = digit(reader.next(), 10);

            while(isDigit(reader.lookahead())) {
              val = val * 10 + digit(reader.next(), 10);
            }

            return success(val);
          }

          return fail("digits is not found", pos, reader);
        }
      };

  public static final PackratParser<String> string =
      new PackratParser<String>() {
        @Override
        protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
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
        protected ParseResult<Character> parse(SourceStringReader reader, Environment env) {
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
      map(rep1(identifier, "."), new Function<List<String>, String>() {
        @Override
        public String apply(List<String> strings) {
          StringBuilder buf = new StringBuilder(strings.get(0));
          for (int i = 1; i < strings.size(); i++) {
            buf.append('.').append(strings.get(i));
          }
          return buf.toString();
        }
      });

  private static final PackratParser<Integer> arrayBrackets =
      map(rep(seq(keyword("["), keyword("]"))), new Function<List<Pair<String, String>>, Integer>() {
        @Override
        public Integer apply(List<Pair<String, String>> pairs) {
          return pairs.size();
        }
      });

  public static final PackratParser<CtClass> className =
      new PackratParser<CtClass>() {
        @Override
        protected ParseResult<CtClass> parse(SourceStringReader reader, Environment env) {
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
      depends(new Function<Environment, PackratParser<CtClass>>() {
        @Override
        public PackratParser<CtClass> apply(final Environment env) {
          return bind(seq(qualifiedIdentifier, arrayBrackets), new Function<Pair<String, Integer>, PackratParser<CtClass>>() {
            @Override
            public PackratParser<CtClass> apply(Pair<String, Integer> pair) {
              try { return unit(env.getArrayType(pair._1, pair._2)); }
              catch (NotFoundError e) { return failure(e.getMessage()); }
            }
          });
        }
      });

  private static int readEscapedChar(SourceStringReader reader) {
    char ch = reader.next();

    if(escapedChars.containsKey(ch)) return escapedChars.get(ch);
    else return -1;
  }

  private static final Map<Character, Character> escapedChars = new HashMap<Character, Character>();

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
}
