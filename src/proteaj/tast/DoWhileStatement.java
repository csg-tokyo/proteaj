package proteaj.tast;

import proteaj.tast.util.*;

public class DoWhileStatement extends Statement {
  public DoWhileStatement(Statement stmt, Expression condition) {
    this.stmt = stmt;
    this.condition = condition;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return null;
  }

  public final Statement stmt;
  public final Expression condition;
}
