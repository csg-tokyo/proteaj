package proteaj.ir.tast;

import javassist.CtClass;

public class ClassName extends TypedAST {
  public ClassName(CtClass cls) {
    this.cls = cls;
  }

  public CtClass getCtClass() {
    return cls;
  }

  @Override
  public String toJavassistCode() {
    return cls.getName();
  }

  @Override
  public String toString() {
    return cls.getName();
  }

  private CtClass cls;
}
