package proteaj.ir.tast;

public class ExpressionStatement extends Statement {
  public ExpressionStatement(Expression expr) {
    this.expr = expr;
  }

  public Expression getExpression() {
    return expr;
  }

  @Override
  public String toJavassistCode() {
    return expr.toJavassistCode() + ';';
  }

  private Expression expr;
}
