package proteaj.tast;

import proteaj.ir.*;

import javassist.*;

public class ClassLiteral extends Expression {
  public ClassLiteral(CtClass cls) {
    super(IRCommonTypes.getClassType());
    this.cls = cls;
  }

  @Override
  public String toJavassistCode() {
    return cls.getName() + ".class";
  }

  private CtClass cls;
}
