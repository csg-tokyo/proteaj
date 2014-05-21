package proteaj.tast;

import proteaj.tast.util.*;
import proteaj.type.CommonTypes;

public class StringLiteral extends Expression {
  public StringLiteral(String str) {
    super(CommonTypes.getInstance().stringType);
    this.str = str;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return "\"" + str + "\"";
  }

  public final String str;
}

