package proteaj.ir.tast;

import javassist.CtClass;

public class ParamAccess extends Expression {
  public ParamAccess(String name, CtClass type, int index) {
    super(type);
    this.name = name;
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public String toJavassistCode() {
    return "$" + (index + 1);
  }

  private String name;
  private int index;
}

