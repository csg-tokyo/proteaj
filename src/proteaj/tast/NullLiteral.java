package proteaj.tast;

import proteaj.ir.*;
import proteaj.tast.util.*;

public class NullLiteral extends Expression {
  public static final NullLiteral instance = new NullLiteral();

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  private NullLiteral() {
    super(IRCommonTypes.getNullType());
  }
}

