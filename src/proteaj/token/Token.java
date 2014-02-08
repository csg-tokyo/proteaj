package proteaj.token;

public abstract class Token {
  public Token(int line) {
    this.line = line;
  }

  public boolean is(char c) { return false; }
  public boolean is(String str) { return false; }

  public boolean isVisible() { return false; }
  public boolean isIdentifier() { return false; }
  public boolean isIntLiteral() { return false; }
  public boolean isStringLiteral() { return false; }

  abstract public String toString();

  public int getLine() {
    return line;
  }

  private int line;
}

