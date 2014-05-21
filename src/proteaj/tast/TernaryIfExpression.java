package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.*;

public class TernaryIfExpression extends Expression {
  public TernaryIfExpression(CtClass clazz, Expression condition, Expression thenExpr, Expression elseExpr) {
    super(clazz);
    this.condition = condition;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression condition;
  public final Expression thenExpr;
  public final Expression elseExpr;
}
