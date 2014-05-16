package proteaj.tast;

import proteaj.tast.util.*;

public class ContinueStatement extends Statement {
  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }
}
