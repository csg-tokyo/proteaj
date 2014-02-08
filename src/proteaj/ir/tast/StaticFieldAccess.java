package proteaj.ir.tast;

import javassist.*;

public class StaticFieldAccess extends Expression {
  public StaticFieldAccess(CtField field) throws NotFoundException {
    super(getType(field));
    this.field = field;
  }

  public CtField getField() {
    return field;
  }

  @Override
  public String toJavassistCode() {
    return field.getDeclaringClass().getName() + '.' + field.getName();
  }

  private static CtClass getType(CtField field) throws NotFoundException {
    return field.getType();
  }

  private CtField field;
}

