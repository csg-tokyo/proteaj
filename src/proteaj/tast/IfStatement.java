package proteaj.tast;

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

  public Expression getCondition() {
    return condition;
  }

  public Statement getThenStmt() {
    return thenStmt;
  }

  public Statement getElseStmt() {
    return elseStmt;
  }

  @Override
  public String toJavassistCode() {
    if(elseStmt != null) return "if(" + condition.toJavassistCode() + ") " + thenStmt.toJavassistCode() + "\nelse " + elseStmt.toJavassistCode();
    else return "if(" + condition.toJavassistCode() + ") " + thenStmt.toJavassistCode();
  }

  private Expression condition;
  private Statement thenStmt;
  private Statement elseStmt;
}
