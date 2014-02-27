package proteaj.tast;

import javassist.*;
import proteaj.tast.util.ExpressionVisitor;

public class StaticMethodCall extends Expression {
  public StaticMethodCall(CtMethod method, Arguments args) throws NotFoundException {
    super(method.getReturnType());
    this.method = method;
    this.args = args;
  }

  @Override
  public String toJavassistCode() {
    return method.getDeclaringClass().getName() + '.' + method.getName() + args.toJavassistCode();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtMethod method;
  public final Arguments args;
}

