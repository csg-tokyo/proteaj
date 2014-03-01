package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtClass;

public class LocalVariable extends Expression {
  public LocalVariable(String name, CtClass type) {
    super(type);
    this.name = name;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final String name;
}

