package proteaj;

import java.util.*;

import proteaj.error.*;
import proteaj.io.SourceFileReader;
import proteaj.token.*;

import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isWhitespace;

public class SigLexer {
  public SigLexer(SourceFileReader reader) throws FileIOError {
    this.filePath = reader.getFilePath();
    this.tokens = tokenizeAll(reader);
    this.eof = new EofToken(reader.getLine());
    init();
  }

  public void init() {
    if(tokens.get(0).isVisible()) current = 0;
    else current = getNextVisibleToken(0);
  }

  public int getPos() {
    return current;
  }

  public void setPos(int pos) {
    current = pos;
  }

  public boolean hasNext() {
    return current < tokens.size();
  }

  public Token lookahead() {
    if(hasNext()) return tokens.get(current);
    else return eof;
  }

  public Token lookahead(int i) {
    int pos = current;
    while(pos < tokens.size()) {
      if(i == 0) return tokens.get(pos);
      else i--;

      pos = getNextVisibleToken(pos);
    }
    return eof;
  }

  public Token next() {
    assert hasNext();
    Token ret = tokens.get(current);
    current = getNextVisibleToken(current);
    return ret;
  }

  public Token next(int i) {
    while(i > 0) {
      next();
      i--;
    }
    return next();
  }

  public void nextLine() {
    int line = tokens.get(current).getLine();

    while(hasNext()) {
      next();
      if(lookahead().getLine() > line) break;
    }
  }

  public String toString(int begin, int end) {
    assert begin < end;
    assert end < tokens.size();

    StringBuilder buf = new StringBuilder();

    for(int i = begin; i <= end; i++) {
      buf.append(tokens.get(i));
    }

    return buf.toString();
  }

  public String getFilePath() {
    return filePath;
  }

  // skip WhiteSpace and BadToken
  private int getNextVisibleToken(int i) {
    i++;

    while(i < tokens.size()) {
      if(tokens.get(i).isVisible()) break;
      else i++;
    }
    return i;
  }

  private List<Token> tokenizeAll(SourceFileReader reader) throws FileIOError {
    List<Token> tokens = new ArrayList<>();

    while(reader.hasNext()) try {
      int c = reader.lookahead();

      if(isWhitespace(c)) tokens.add(tokenizeWhiteSpaces(reader));
      else if(isJavaIdentifierStart(c)) tokens.add(tokenizeIdentifier(reader));
      else if(isDigit(c)) tokens.add(tokenizeIntLiteral(reader));
      else if(c == '\'') tokens.add(tokenizeCharLiteral(reader));
      else if(c == '\"') tokens.add(tokenizeStringLiteral(reader));
      else if(c == '/')  tokens.add(tokenizeCommentOrSlash(reader));
      else tokens.add(tokenizeSymbol(reader));
    } catch (LexicalError e) {
      ErrorList.addError(e);
      tokens.add(new BadToken(reader.getLine()));
      reader.nextLine();
    }

    return tokens;
  }

  private WhiteSpaces tokenizeWhiteSpaces(SourceFileReader reader) throws FileIOError {
    assert isWhitespace(reader.lookahead());

    int line = reader.getLine();
    StringBuilder buf = new StringBuilder();

    while(isWhitespace(reader.lookahead())) {
      buf.append(reader.next());
    }

    return new WhiteSpaces(buf.toString(), line);
  }

  private Identifier tokenizeIdentifier(SourceFileReader reader) throws FileIOError {
    assert isJavaIdentifierStart(reader.lookahead());

    int line = reader.getLine();
    StringBuilder buf = new StringBuilder();

    buf.append(reader.next());

    while(isJavaIdentifierPart(reader.lookahead())) {
      buf.append(reader.next());
    }

    return new Identifier(buf.toString(), line);
  }

  private IntLiteral tokenizeIntLiteral(SourceFileReader reader) throws FileIOError {
    assert isDigit(reader.lookahead());

    int line = reader.getLine();
    StringBuilder buf = new StringBuilder();

    while(isDigit(reader.lookahead())) {
      buf.append(reader.next());
    }

    return new IntLiteral(buf.toString(), line);
  }

  private CharLiteral tokenizeCharLiteral(SourceFileReader reader) throws FileIOError, LexicalError {
    assert reader.lookahead() == '\'';

    int line = reader.getLine();
    reader.next();

    if(reader.hasNext()) {
      char c = reader.next();

      if(c == '\n' || c == '\'') {
        throw new LexicalError("invalid char literal", filePath, line);
      }

      if(c == '\\') {
        c = readEscapedChar(reader);
      }

      if(reader.lookahead() == '\'') {
        reader.next();
        return new CharLiteral(c, line);
      }
    }

    throw new LexicalError("invalid char literal", filePath, line);
  }

  private StringLiteral tokenizeStringLiteral(SourceFileReader reader) throws FileIOError, LexicalError {
    assert reader.lookahead() == '\"';

    int line = reader.getLine();
    StringBuilder buffer = new StringBuilder();

    reader.next();

    while(reader.hasNext()) {
      char ch = reader.next();

      switch(ch) {
        case '\n': throw new LexicalError("String literal is not properly closed by a double-quote", filePath, line);
        case '\"': return new StringLiteral(buffer.toString(), line);
        case '\\': ch = readEscapedChar(reader);
        default: buffer.append(ch);
      }
    }

    throw new LexicalError("String literal is not properly closed by a double-quote", filePath, line);
  }

  private Token tokenizeCommentOrSlash(SourceFileReader reader) throws FileIOError, LexicalError {
    assert reader.lookahead() == '/';

    int line = reader.getLine();
    reader.next();

    // line comment
    if(reader.lookahead() == '/') {
      reader.next();
      while(reader.hasNext()) {
        if(reader.next() == '\n') break;
      }
      return new WhiteSpaces("\n", line);
    }
    // block comment
    else if(reader.lookahead() == '*') {
      int count = 0;
      reader.next();
      while(reader.hasNext()) {
        char ch = reader.next();
        if(ch == '\n') count++;
        else if(ch == '*' && reader.lookahead() == '/') {
          reader.next();
          if(count == 0) return new WhiteSpaces(" ", line);

          StringBuilder buf = new StringBuilder();
          for(int i = 0; i < count; i++) buf.append('\n');
          return new WhiteSpaces(buf.toString(), line);
        }
      }
      throw new LexicalError("end of comment is not found", filePath, line);
    }
    // slash
    else return new Symbol('/', line);
  }

  private Symbol tokenizeSymbol(SourceFileReader reader) throws FileIOError {
    assert reader.hasNext();

    int line = reader.getLine();
    char ch = reader.next();

    return new Symbol(ch, line);
  }

  private char readEscapedChar(SourceFileReader reader) throws FileIOError, LexicalError {
    char ch = reader.next();

    if(escapedChars.containsKey(ch)) return escapedChars.get(ch);
    else throw new LexicalError("invalid escape sequence \"\\" + ch + "\"", filePath, reader.getLine());
  }

  private int current;
  private List<Token> tokens;
  private EofToken eof;

  private String filePath;

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
}
