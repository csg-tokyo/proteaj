package proteaj.ir.tast;

public class ThrowStatement extends Statement {
  public ThrowStatement(Expression e) {
    this.e = e;
  }

  public Expression getThrowException() {
    return e;
  }

  @Override
  public String toJavassistCode() {
    return "throw " + e.toJavassistCode() + ';';
  }

  private Expression e;
}

