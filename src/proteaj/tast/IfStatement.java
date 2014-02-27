package proteaj.tast;

import proteaj.tast.util.*;

public class IfStatement extends Statement {
  public IfStatement(Expression condition, Statement thenStmt) {
    this.condition = condition;
    this.thenStmt = thenStmt;
    this.elseStmt = null;
  }

  public IfStatement(Expression condition, Statement thenStmt, Statement elseStmt) {
    this.condition = condition;
    this.thenStmt = thenStmt;
    this.elseStmt = elseStmt;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression condition;
  public final Statement thenStmt;
  public final Statement elseStmt;
}
