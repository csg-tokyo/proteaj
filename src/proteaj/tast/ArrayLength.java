package proteaj.tast;

import proteaj.tast.util.ExpressionVisitor;

import javassist.CtClass;

public class ArrayLength extends Expression {
  public ArrayLength(Expression array) {
    super(CtClass.intType);
    this.array = array;
  }

  @Override
  public String toJavassistCode() {
    return array.toJavassistCode() + ".length";
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression array;
}

