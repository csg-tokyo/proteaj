package proteaj.ir.tast;

import javassist.*;

public class TypeName extends TypedAST {
  public TypeName(CtClass type) {
    this.type = type;
  }

  public CtClass getType() {
    return type;
  }

  @Override
  public String toJavassistCode() {
    return type.getName();
  }

  private CtClass type;
}
