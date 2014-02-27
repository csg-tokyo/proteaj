package proteaj.tast;

import proteaj.tast.util.*;

public class WhileStatement extends Statement {
  public WhileStatement(Expression condition, Statement stmt) {
    this.condition = condition;
    this.stmt = stmt;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression condition;
  public final Statement stmt;
}

