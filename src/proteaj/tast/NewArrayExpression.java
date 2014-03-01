package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class NewArrayExpression extends Expression {
  public NewArrayExpression(CtClass arrayType, List<Expression> args) {
    super(arrayType);
    this.args = args;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> args;
}

