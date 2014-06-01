package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;

public class DoubleLiteral extends Expression {
  public DoubleLiteral(double val) {
    super(CtClass.doubleType);
    this.val = val;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }

  public final double val;
}