package proteaj.ir.tast;

public class AssignExpression extends Expression {

  public AssignExpression(Expression ref, Expression val) {
    super(ref.getType());
    this.ref = ref;
    this.val = val;
  }

  public Expression getLeft() {
    return ref;
  }

  public Expression getRight() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    return ref.toJavassistCode() + " = " + val.toJavassistCode();
  }

  private Expression ref;
  private Expression val;

}

