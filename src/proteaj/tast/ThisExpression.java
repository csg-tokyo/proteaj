package proteaj.tast;

import javassist.*;

public class ThisExpression extends Expression {
  public ThisExpression(CtClass thisClass) {
    super(thisClass);
  }

  @Override
  public String toJavassistCode() {
    return "this";
  }
}

