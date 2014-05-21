package proteaj.tast;

import proteaj.tast.util.*;
import proteaj.type.CommonTypes;

public class NullLiteral extends Expression {
  public static NullLiteral getInstance() {
    if (instance == null) instance = new NullLiteral();
    return instance;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  private NullLiteral() {
    super(CommonTypes.getInstance().nullType);
  }

  private static NullLiteral instance = null;
}

