package proteaj.tast;

import javassist.CtClass;

public class LocalVariable extends Expression {
  public LocalVariable(String name, CtClass type) {
    super(type);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toJavassistCode() {
    return name;
  }

  private String name;
}

