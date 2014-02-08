package proteaj.token;

public class WhiteSpaces extends Token {
  public WhiteSpaces(String str, int line) {
    super(line);
    this.str = str;
  }

  @Override
  public String toString() {
    return str;
  }

  private String str;
}
