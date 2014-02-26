package proteaj.tast;

import javassist.CtClass;

public class BooleanLiteral extends Expression {
  public BooleanLiteral(boolean val) {
    super(CtClass.booleanType);
    this.val = val;
  }

  public boolean getValue() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    if(val == true) return "true";
    else return "false";
  }

  private boolean val;
}
