package proteaj.tast;

import javassist.CtConstructor;

public class NewExpression extends Expression {
  public NewExpression(CtConstructor constructor, Arguments args) {
    super(constructor.getDeclaringClass());
    this.constructor = constructor;
    this.args = args;
  }

  public CtConstructor getConstructor() {
    return constructor;
  }

  public Arguments getArgs() {
    return args;
  }

  @Override
  public String toJavassistCode() {
    return "new " + constructor.getDeclaringClass().getName() + args.toJavassistCode();
  }

  private CtConstructor constructor;
  private Arguments args;
}
