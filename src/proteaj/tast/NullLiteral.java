package proteaj.tast;

import proteaj.ir.*;
import proteaj.tast.util.ExpressionVisitor;

public class NullLiteral extends Expression {
  public static final NullLiteral instance = new NullLiteral();

  @Override
  public String toJavassistCode() {
    return "null";
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  private NullLiteral() {
    super(IRCommonTypes.getNullType());
  }
}

