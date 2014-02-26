package proteaj.tast;

public class DefaultValue {
  public DefaultValue(Expression expr) {
    this.expr = expr;
  }

  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();
    buf.append("{\n");
    buf.append("return ").append(expr.toJavassistCode()).append(';');
    buf.append("\n}");
    return buf.toString();
  }

  public final Expression expr;
}

