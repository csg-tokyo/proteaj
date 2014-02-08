package proteaj.ir.tast;

import javassist.*;

public class StaticMethodCall extends Expression {
  public StaticMethodCall(CtMethod method, Arguments args) throws NotFoundException {
    super(getReturnType(method));
    this.method = method;
    this.args = args;
  }

  public CtMethod getMethod() {
    return method;
  }

  public Arguments getArgs() {
    return args;
  }

  @Override
  public String toJavassistCode() {
    return method.getDeclaringClass().getName() + '.' + method.getName() + args.toJavassistCode();
  }

  private static CtClass getReturnType(CtMethod method) throws NotFoundException {
    return method.getReturnType();
  }

  private CtMethod method;
  private Arguments args;
}

