package proteaj.tast;

import javassist.CtClass;

public class IntLiteral extends Expression {
  public IntLiteral(int val) {
    super(CtClass.intType);
    this.val = val;
  }

  public int getValue() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    return String.valueOf(val);
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }

  private int val;
}

