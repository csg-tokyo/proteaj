package proteaj.tast;

import proteaj.tast.util.ExpressionVisitor;

public class AssignExpression extends Expression {

  public AssignExpression(Expression ref, Expression val) {
    super(ref.getType());
    this.ref = ref;
    this.val = val;
  }

  @Override
  public String toJavassistCode() {
    return ref.toJavassistCode() + " = " + val.toJavassistCode();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression ref;
  public final Expression val;

}

