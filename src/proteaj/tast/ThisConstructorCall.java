package proteaj.tast;

import proteaj.tast.util.*;

import javassist.CtConstructor;

public class ThisConstructorCall extends Statement {

  public ThisConstructorCall(CtConstructor constructor, Arguments args) {
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

