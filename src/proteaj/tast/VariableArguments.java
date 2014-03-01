package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class VariableArguments extends Expression {
  public VariableArguments(CtClass arrayType) {
    super(arrayType);
    this.args = Collections.emptyList();
  }

  public VariableArguments(List<Expression> args, CtClass arrayType) {
    super(arrayType);
    this.args = args;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> args;
}

