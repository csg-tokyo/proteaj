package proteaj.tast;

import javassist.*;
import proteaj.tast.util.*;

public class SuperExpression extends Expression {
  public SuperExpression(CtClass thisClass) throws NotFoundException {
    super(thisClass.getSuperclass());
    this.thisClass = thisClass;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass thisClass;
}

