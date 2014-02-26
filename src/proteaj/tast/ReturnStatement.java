package proteaj.tast;

public class ReturnStatement extends Statement {
  public ReturnStatement() {
    this.val = null;
  }

  public ReturnStatement(Expression val) {
    this.val = val;
  }

  public Expression getReturnValue() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    if(val != null) return "return " + val.toJavassistCode() + ';';
    else return "return;";
  }

  private Expression val;
}

