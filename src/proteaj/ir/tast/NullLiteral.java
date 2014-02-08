package proteaj.ir.tast;

import proteaj.ir.*;

public class NullLiteral extends Expression {
  public static final NullLiteral instance = new NullLiteral();

  @Override
  public String toJavassistCode() {
    return "null";
  }

  private NullLiteral() {
    super(IRCommonTypes.getNullType());
  }
}

