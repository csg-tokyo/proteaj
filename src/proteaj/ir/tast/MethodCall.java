package proteaj.ir.tast;

import javassist.*;

public class MethodCall extends Expression {
  public MethodCall(Expression expr, CtMethod method, Arguments args) throws NotFoundException {
    super(getReturnType(method));
    this.expr = expr;
    this.method = method;
    this.args = args;
  }

  public Expression getReceiver() {
    return expr;
  }

  public CtMethod getMethod() {
    return method;
  }

  public Arguments getArgs() {
    return args;
  }

  @Override
  public String toJavassistCode() {
    return expr.toJavassistCode() + "." + method.getName() + args.toJavassistCode();
  }

  private static CtClass getReturnType(CtMethod method) throws NotFoundException {
    return method.getReturnType();
  }

  private Expression expr;
  private CtMethod method;
  private Arguments args;
}
