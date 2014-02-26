package proteaj.tast;

import javassist.CtClass;

public abstract class Expression extends TypedAST {
  public Expression(CtClass type) {
    this.type = type;
  }

  public CtClass getType() {
    return type;
  }

  private CtClass type;
}

