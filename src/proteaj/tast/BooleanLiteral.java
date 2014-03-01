package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.*;

public class BooleanLiteral extends Expression {
  public BooleanLiteral(boolean val) {
    super(CtClass.booleanType);
    this.val = val;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final boolean val;
}
