package proteaj.tast;

import proteaj.tast.util.*;

import javassist.*;

public class ThisExpression extends Expression {
  public ThisExpression(CtClass thisClass) {
    super(thisClass);
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }
}

