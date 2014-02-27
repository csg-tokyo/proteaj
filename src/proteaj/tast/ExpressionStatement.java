package proteaj.tast;

import proteaj.tast.util.*;

public class ExpressionStatement extends Statement {
  public ExpressionStatement(Expression expr) {
    this.expr = expr;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression expr;
}
