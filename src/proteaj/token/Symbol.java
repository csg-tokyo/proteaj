package proteaj.token;

public class Symbol extends Token {
  public Symbol(char c, int line) {
    super(line);
    this.c = c;
  }

  @Override
  public boolean is(char c) {
    return this.c == c;
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public String toString() {
    return String.valueOf(c);
  }

  private char c;
}

