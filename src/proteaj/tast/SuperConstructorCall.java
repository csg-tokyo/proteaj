package proteaj.tast;

import javassist.CtConstructor;

public class SuperConstructorCall extends Statement {

  public SuperConstructorCall(CtConstructor constructor, Arguments args) {
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
    return "super" + args.toJavassistCode() + ';';
  }

  private CtConstructor constructor;
  private Arguments args;
}

