package proteaj.tast;

import javassist.CtClass;

public abstract class Expression {
  public Expression(CtClass type) {
    this.type = type;
  }

  public CtClass getType() {
    return type;
  }

  public abstract String toJavassistCode();

  private CtClass type;
}

