package proteaj.tast;

import javassist.*;
import proteaj.tast.util.*;

public class FieldAccess extends Expression {
  public FieldAccess(Expression expr, CtField field) throws NotFoundException {
    super(field.getType());
    this.expr = expr;
    this.field = field;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return expr.toString() + '.' + field.getName();
  }

  public final Expression expr;
  public final CtField field;
}

