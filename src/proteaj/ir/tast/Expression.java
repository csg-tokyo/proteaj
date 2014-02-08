package proteaj.ir.tast;

import javassist.CtClass;
import java.util.List;

public abstract class Expression extends TypedAST {
  public Expression(CtClass type) {
    this.type = type;
  }

  public CtClass getType() {
    return type;
  }

  private CtClass type;
}

