package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;

import static java.lang.Character.isWhitespace;
import static java.lang.Character.isDigit;
import static java.lang.Character.digit;

public class LiteralParser extends PackratParser<Expression> {
  /* Literal
   *  : IntLiteral
   *  | BooleanLiteral
   *  | StringLiteral
   *  | CharLiteral
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> intLiteral = parseIntLiteral(reader, env, pos);
    if(! intLiteral.isFail()) return intLiteral;

    ParseResult<Expression> boolLiteral = parseBooleanLiteral(reader, env, pos);
    if(! boolLiteral.isFail()) return boolLiteral;

    ParseResult<Expression> strLiteral = parseStringLiteral(reader, env, pos);
    if(! strLiteral.isFail()) return strLiteral;

    ParseResult<Expression> chrLiteral = parseCharLiteral(reader, env, pos);
    if(! chrLiteral.isFail()) return chrLiteral;

    return fail(Arrays.<ParseResult<?>>asList(intLiteral, boolLiteral, strLiteral, chrLiteral), pos, reader);
  }

  /* IntLiteral
   *  : Non0Digit { Digit }
   *  | 0
   */
  private ParseResult<Expression> parseIntLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    while(isWhitespace(reader.lookahead())) reader.next();

    if(isDigit(reader.lookahead())) {
      if(reader.lookahead() == 0) {
        reader.next();
        return success(new IntLiteral(0));
      }

      int val = digit(reader.next(), 10);

      while(isDigit(reader.lookahead())) {
        val = val * 10 + digit(reader.next(), 10);
      }

      return success(new IntLiteral(val));
    }

    return fail("digits is not found", pos, reader);
  }

  /* BooleanLiteral
   *  : "true"
   *  | "false"
   */
  private ParseResult<Expression> parseBooleanLiteral(SourceStringReader reader, Environment env, int pos) {
    // "true"
    ParseResult<String> trueKeyword = KeywordParser.getParser("true").applyRule(reader, env, pos);
    if(! trueKeyword.isFail()) return success(new BooleanLiteral(true));

    // "false"
    ParseResult<String> falseKeyword = KeywordParser.getParser("false").applyRule(reader, env, pos);
    if(! falseKeyword.isFail()) return success(new BooleanLiteral(false));

    return fail("boolean literals are not found", pos, reader);
  }

  /* StringLiteral
   *  : '"' { any } '"'
   */
  private ParseResult<Expression> parseStringLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    // '"'
    ParseResult<String> bdq = KeywordParser.getParser("\"").applyRule(reader, env);
    if(bdq.isFail()) return fail(bdq, pos, reader);

    StringBuilder buf = new StringBuilder();

    while(reader.hasNext() && reader.lookahead() != '\n') {
      int ch = reader.next();
      switch(ch) {
        case '"' : return success(new StringLiteral(buf.toString()));
        case '\\':
          ch = readEscapedChar(reader);
          if(ch == -1) return fail("invalid escape character", pos, reader);
        default: buf.append((char)ch);
      }
    }

    return fail("unterminated string literal", pos, reader);
  }

  /* CharLiteral
   *  : '\'' ( Character | EscapedCharacter ) '\''
   */
  private ParseResult<Expression> parseCharLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    // '\''
    ParseResult<String> bquote = KeywordParser.getParser("\'").applyRule(reader, env);
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
        return success(new CharLiteral((char)ch));
      }
    }

    return fail("unterminated character literal", pos, reader);
  }

  private int readEscapedChar(SourceStringReader reader) {
    char ch = reader.next();

    if(escapedChars.containsKey(ch)) return escapedChars.get(ch);
    else return -1;
  }

  @Override
  public String toString() {
    return "LiteralParser";
  }

  public static final LiteralParser parser = new LiteralParser();

  private LiteralParser() {}

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

