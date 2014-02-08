package proteaj.token;

public class BadToken extends Token {
  public BadToken(int line) {
    super(line);
  }

  @Override
  public String toString() {
    return "";
  }
}

