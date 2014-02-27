package proteaj.tast;

import proteaj.tast.util.*;

public class ForStatement extends Statement {
  public ForStatement(Expression init, Expression cond, Expression update, Statement stmt) {
    this.init = init;
    this.cond = cond;
    this.update = update;
    this.stmt = stmt;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression init;
  public final Expression cond;
  public final Expression update;
  public final Statement stmt;
}

