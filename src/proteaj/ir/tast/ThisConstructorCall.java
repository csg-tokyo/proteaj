package proteaj.ir.tast;

import javassist.CtConstructor;

public class ThisConstructorCall extends Statement {

  public ThisConstructorCall(CtConstructor constructor, Arguments args) {
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
    return "this" + args.toJavassistCode() + ';';
  }

  private CtConstructor constructor;
  private Arguments args;

}

