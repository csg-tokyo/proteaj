package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtClass;

public class IntLiteral extends Expression {
  public IntLiteral(int val) {
    super(CtClass.intType);
    this.val = val;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }

  public final int val;
}

