package proteaj.tast;

import proteaj.ir.*;
import proteaj.tast.util.*;

import java.util.*;

public class Operation extends Expression {
  public Operation(IROperator operator, List<Expression> operands) {
    super(operator.getReturnType());
    this.operator = operator;
    this.operands = operands;
  }

  public IROperator getOperator() {
    return operator;
  }

  public List<Expression> getOperands() {
    return operands;
  }

  @Override
  public String toJavassistCode() {
    return operator.toJavassistCode(operands);
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return operator.toString(operands);
  }

  private IROperator operator;
  private List<Expression> operands;
}

