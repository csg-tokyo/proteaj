package proteaj.tast;

public class ForStatement extends Statement {
  public ForStatement(Expression init, Expression cond, Expression update, Statement stmt) {
    this.init = init;
    this.cond = cond;
    this.update = update;
    this.stmt = stmt;
  }

  @Override
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();

    buf.append("for").append('(');
    buf.append(init.toJavassistCode()).append(';');
    buf.append(cond.toJavassistCode()).append(';');
    buf.append(update.toJavassistCode()).append(')');
    buf.append(stmt.toJavassistCode());

    return buf.toString();
  }

  private Expression init;
  private Expression cond;
  private Expression update;
  private Statement stmt;
}

