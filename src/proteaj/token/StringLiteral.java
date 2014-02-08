package proteaj.token;

import static proteaj.util.Escape.*;

public class StringLiteral extends Token {
  public StringLiteral(String str, int line) {
    super(line);
    this.str = str;
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public boolean isStringLiteral() {
    return true;
  }

  @Override
  public String toString() {
    return "\"" + escape(str) + "\"";
  }

  public String getValue() {
    return str;
  }

  private String str;
}

