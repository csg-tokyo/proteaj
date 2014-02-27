package proteaj.tast;

import proteaj.tast.util.*;

public class ReturnStatement extends Statement {
  public ReturnStatement() {
    this.value = null;
  }

  public ReturnStatement(Expression val) {
    this.value = val;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression value;
}

