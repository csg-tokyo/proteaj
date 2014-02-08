package proteaj.ir.tast;

import javassist.CtClass;

public class ArrayLength extends Expression {
  public ArrayLength(Expression array) {
    super(CtClass.intType);
    this.array = array;
  }

  public Expression getArray() {
    return array;
  }

  @Override
  public String toJavassistCode() {
    return array.toJavassistCode() + ".length";
  }

  private Expression array;
}

