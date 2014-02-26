package proteaj.tast;

import javassist.*;

public class ArrayAccess extends Expression {
  public ArrayAccess(Expression array, Expression index) throws NotFoundException {
    super(getComponentType(array.getType()));
    this.array = array;
    this.index = index;
  }

  public Expression getArray() {
    return array;
  }

  public Expression getIndex() {
    return index;
  }

  private static CtClass getComponentType(CtClass arrayType) throws NotFoundException {
    return arrayType.getComponentType();
  }

  @Override
  public String toJavassistCode() {
    return array.toJavassistCode() + '[' + index.toJavassistCode() + ']';
  }

  private Expression array;
  private Expression index;
}

