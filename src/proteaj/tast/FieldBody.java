package proteaj.tast;

public class FieldBody extends TypedAST {
  public FieldBody(Expression expr) {
    this.expr = expr;
  }

  public Expression getExpression() {
    return expr;
  }

  @Override
  public String toJavassistCode() {
    return expr.toJavassistCode();
  }

  private Expression expr;
}

