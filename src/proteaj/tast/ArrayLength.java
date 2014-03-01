package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtClass;

public class ArrayLength extends Expression {
  public ArrayLength(Expression array) {
    super(CtClass.intType);
    this.array = array;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression array;
}

