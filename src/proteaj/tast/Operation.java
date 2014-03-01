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

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return operator.toString(operands);
  }

  public final IROperator operator;
  public final List<Expression> operands;
}

