package proteaj.ir.tast;

import javassist.*;

public class SuperExpression extends Expression {
  public SuperExpression(CtClass thisClass) throws NotFoundException {
    super(getSuperClass(thisClass));
    this.thisClass = thisClass;
  }

  public CtClass getThisClass() {
    return thisClass;
  }

  @Override
  public String toJavassistCode() {
    return "super";
  }

  private static CtClass getSuperClass(CtClass cls) throws NotFoundException {
    return cls.getSuperclass();
  }

  private CtClass thisClass;
}

