package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;

public class BooleanLiteral extends Expression {
  public BooleanLiteral(boolean val) {
    super(CtClass.booleanType);
    this.val = val;
  }

  @Override
  public String toJavassistCode() {
    if(val == true) return "true";
    else return "false";
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final boolean val;
}
