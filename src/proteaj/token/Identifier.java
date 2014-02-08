package proteaj.token;

public class Identifier extends Token {
  public Identifier(String str, int line) {
    super(line);
    this.str = str;
  }

  @Override
  public boolean is(String str) {
    return this.str.equals(str);
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public boolean isIdentifier() {
    return true;
  }

  @Override
  public String toString() {
    return str;
  }

  private String str;
}

