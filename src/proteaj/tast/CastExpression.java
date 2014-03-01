package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.*;

public class CastExpression extends Expression {
  public CastExpression(CtClass type, Expression expr) {
    super(type);
    this.expr = expr;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression expr;
}
