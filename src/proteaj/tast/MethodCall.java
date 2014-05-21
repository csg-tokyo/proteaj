package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import java.util.stream.Collectors;

import javassist.*;

public class MethodCall extends Expression {
  public MethodCall(Expression expr, CtMethod method, List<Expression> args) throws NotFoundException {
    super(method.getReturnType());
    this.expr = expr;
    this.method = method;
    this.args = args;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return expr.toString() + '.' + method.getName() + args.stream().map(Object::toString).collect(Collectors.joining(",", "(", ")"));
  }

  public final Expression expr;
  public final CtMethod method;
  public final List<Expression> args;
}
