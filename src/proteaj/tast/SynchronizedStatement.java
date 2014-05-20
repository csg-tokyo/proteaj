package proteaj.tast;

import proteaj.tast.util.StatementVisitor;

public class SynchronizedStatement extends Statement {
  public SynchronizedStatement(Expression expr, Block block) {
    this.expr = expr;
    this.block = block;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression expr;
  public final Block block;
}
