package proteaj.tast;

public class WhileStatement extends Statement {
  public WhileStatement(Expression condition, Statement stmt) {
    this.condition = condition;
    this.stmt = stmt;
  }

  public Expression getCondition() {
    return condition;
  }

  public Statement getStatement() {
    return stmt;
  }

  @Override
  public String toJavassistCode() {
    return "while ( " + condition.toJavassistCode() + " ) " + stmt.toJavassistCode();
  }

  private Expression condition;
  private Statement stmt;
}

