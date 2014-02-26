package proteaj.tast;

import javassist.*;

public class FieldAccess extends Expression {
  public FieldAccess(Expression expr, CtField field) throws NotFoundException {
    super(getType(field));
    this.expr = expr;
    this.field = field;
  }

  public Expression getReceiver() {
    return expr;
  }

  public CtField getField() {
    return field;
  }

  @Override
  public String toJavassistCode() {
    return expr.toJavassistCode() + '.' + field.getName();
  }

  private static CtClass getType(CtField field) throws NotFoundException {
    return field.getType();
  }

  private Expression expr;
  private CtField field;
}

