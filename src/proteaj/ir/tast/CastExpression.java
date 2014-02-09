package proteaj.ir.tast;

import javassist.CtClass;

public class CastExpression extends Expression {
  public CastExpression(CtClass type, Expression expr) {
    super(type);
    this.expr = expr;
  }

  @Override
  public String toJavassistCode() {
    if (this.getType() == CtClass.voidType) return expr.toJavassistCode();
    else return "((" + this.getType().getName() + ")" + expr.toJavassistCode() + ")";
  }

  private Expression expr;
}
