package proteaj.tast;

import proteaj.tast.util.*;

import java.util.List;

public class SwitchStatement extends Statement {
  public SwitchStatement (Expression expr, List<CaseBlock> cases) {
    this.expr = expr;
    this.cases = cases;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression expr;
  public final List<CaseBlock> cases;
}
