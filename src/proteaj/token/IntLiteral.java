package proteaj.token;

public class IntLiteral extends Token {
  public IntLiteral(String str, int line) {
    super(line);
    this.str = str;
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public boolean isIntLiteral() {
    return true;
  }

  @Override
  public String toString() {
    return str;
  }

  private String str;
}

