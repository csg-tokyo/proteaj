package proteaj.tast;

public class FieldBody {
  public FieldBody(Expression expr) {
    this.expr = expr;
  }

  public String toJavassistCode() {
    return expr.toJavassistCode();
  }

  public final Expression expr;
}

