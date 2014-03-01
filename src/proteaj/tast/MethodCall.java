package proteaj.tast;

import javassist.*;
import proteaj.tast.util.*;

public class MethodCall extends Expression {
  public MethodCall(Expression expr, CtMethod method, Arguments args) throws NotFoundException {
    super(method.getReturnType());
    this.expr = expr;
    this.method = method;
    this.args = args;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Expression expr;
  public final CtMethod method;
  public final Arguments args;
}
