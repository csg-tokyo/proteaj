package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class ThisConstructorCall extends Statement {

  public ThisConstructorCall(CtConstructor constructor, List<Expression> args) {
    this.constructor = constructor;
    this.args = args;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtConstructor constructor;
  public final List<Expression> args;

}

