package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtConstructor;

public class SuperConstructorCall extends Statement {

  public SuperConstructorCall(CtConstructor constructor, Arguments args) {
    this.constructor = constructor;
    this.args = args;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtConstructor constructor;
  public final Arguments args;
}

