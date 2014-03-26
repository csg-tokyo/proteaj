package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtClass;

public abstract class Expression {
  public Expression(CtClass type) {
    this.type = type;
  }

  public abstract <T> T accept (ExpressionVisitor<T> visitor, T t);

  public final CtClass type;
}
