package proteaj.tast;

import proteaj.tast.util.*;

public class ThrowStatement extends Statement {
  public ThrowStatement(Expression e) {
    this.e = e;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression e;
}

