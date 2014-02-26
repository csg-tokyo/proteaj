package proteaj.tast;

import proteaj.ir.*;

import static proteaj.util.Escape.*;

public class StringLiteral extends Expression {
  public StringLiteral(String str) {
    super(IRCommonTypes.getStringType());
    this.str = str;
  }

  public String getString() {
    return str;
  }

  @Override
  public String toJavassistCode() {
    return '"' + escape(str) + '"';
  }

  @Override
  public String toString() {
    return "\"" + str + "\"";
  }

  private String str;
}

