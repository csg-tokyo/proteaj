package proteaj.tast;

import proteaj.tast.util.*;

import javassist.*;

public class ArrayAccess extends Expression {
  public ArrayAccess(Expression array, Expression index) throws NotFoundException {
    super(array.type.getComponentType());
    this.array = array;
    this.index = index;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression array;
  public final Expression index;
}

