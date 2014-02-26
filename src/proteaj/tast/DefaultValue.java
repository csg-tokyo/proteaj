package proteaj.tast;

public class DefaultValue extends TypedAST {
  public DefaultValue(Expression expr) {
    this.expr = expr;
  }

  @Override
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();
    buf.append("{\n");
    buf.append("return ").append(expr.toJavassistCode()).append(';');
    buf.append("\n}");
    return buf.toString();
  }

  private Expression expr;
}

