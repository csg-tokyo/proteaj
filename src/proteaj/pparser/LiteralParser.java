package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;

import static java.lang.Character.isWhitespace;
import static java.lang.Character.isDigit;
import static java.lang.Character.digit;

public class LiteralParser extends PackratParser {
  /* Literal
   *  : IntLiteral
   *  | BooleanLiteral
   *  | StringLiteral
   *  | CharLiteral
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST intLiteral = parseIntLiteral(reader, env, pos);
    if(! intLiteral.isFail()) return intLiteral;

    TypedAST boolLiteral = parseBooleanLiteral(reader, env, pos);
    if(! boolLiteral.isFail()) return boolLiteral;

    TypedAST strLiteral = parseStringLiteral(reader, env, pos);
    if(! strLiteral.isFail()) return strLiteral;

    TypedAST chrLiteral = parseCharLiteral(reader, env, pos);
    if(! chrLiteral.isFail()) return chrLiteral;

    FailLog flog = chooseBest(intLiteral.getFailLog(), boolLiteral.getFailLog(), strLiteral.getFailLog(), chrLiteral.getFailLog());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  /* IntLiteral
   *  : Non0Digit { Digit }
   *  | 0
   */
  private TypedAST parseIntLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    while(isWhitespace(reader.lookahead())) reader.next();

    if(isDigit(reader.lookahead())) {
      if(reader.lookahead() == 0) {
        reader.next();
        return new IntLiteral(0);
      }

      int val = digit(reader.next(), 10);

      while(isDigit(reader.lookahead())) {
        val = val * 10 + digit(reader.next(), 10);
      }

      return new IntLiteral(val);
    }

    FailLog flog = new FailLog("digits is not found", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  /* BooleanLiteral
   *  : "true"
   *  | "false"
   */
  private TypedAST parseBooleanLiteral(SourceStringReader reader, Environment env, int pos) {
    // "true"
    TypedAST trueKeyword = KeywordParser.getParser("true").applyRule(reader, env, pos);
    if(! trueKeyword.isFail()) return new BooleanLiteral(true);

    // "false"
    TypedAST falseKeyword = KeywordParser.getParser("false").applyRule(reader, env, pos);
    if(! falseKeyword.isFail()) return new BooleanLiteral(false);

    reader.setPos(pos);
    FailLog flog = new FailLog("boolean literals are not found", reader.getPos(), reader.getLine());
    return new BadAST(flog);
  }

  /* StringLiteral
   *  : '"' { any } '"'
   */
  private TypedAST parseStringLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    // '"'
    TypedAST bdq = KeywordParser.getParser("\"").applyRule(reader, env);
    if(bdq.isFail()) {
      reader.setPos(pos);
      return new BadAST(bdq.getFailLog());
    }

    StringBuilder buf = new StringBuilder();

    while(reader.hasNext() && reader.lookahead() != '\n') {
      int ch = reader.next();
      switch(ch) {
        case '"' : return new StringLiteral(buf.toString());
        case '\\':
          ch = readEscapedChar(reader);
          if(ch == -1) {
            FailLog flog = new FailLog("invalid escape character", reader.getPos(), reader.getLine());
            reader.setPos(pos);
            return new BadAST(flog);
          }
        default: buf.append((char)ch);
      }
    }

    FailLog flog = new FailLog("unterminated string literal", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  /* CharLiteral
   *  : '\'' ( Character | EscapedCharacter ) '\''
   */
  private TypedAST parseCharLiteral(SourceStringReader reader, Environment env, int pos) {
    reader.setPos(pos);

    // '\''
    TypedAST bquote = KeywordParser.getParser("\'").applyRule(reader, env);
    if(bquote.isFail()) {
      reader.setPos(pos);
      return new BadAST(bquote.getFailLog());
    }

    if(reader.hasNext() && reader.lookahead() != '\n') {
      int ch = reader.next();

      if(ch == '\'') {
        FailLog flog = new FailLog("invalid character literal", reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }
      else if(ch == '\\') {
        ch = readEscapedChar(reader);
        if(ch == -1) {
          FailLog flog = new FailLog("invalid escape character", reader.getPos(), reader.getLine());
          reader.setPos(pos);
          return new BadAST(flog);
        }
      }

      if(reader.lookahead() == '\'') {
        reader.next();
        return new CharLiteral((char)ch);
      }
    }

    FailLog flog = new FailLog("unterminated character literal", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
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

