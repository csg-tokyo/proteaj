package proteaj.tast;

import javassist.CtClass;

public class LocalVarDecl extends Expression {
  public LocalVarDecl(CtClass type, String name) {
    super(CtClass.voidType);
    this.type = type;
    this.name = name;
    this.val = null;
  }

  public LocalVarDecl(CtClass type, String name, Expression val) {
    super(CtClass.voidType);
    this.type = type;
    this.name = name;
    this.val = val;
  }

  public CtClass getVariableType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public Expression getInitialValue() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    if(val != null) return type.getName() + " " + name + " = " + val.toJavassistCode();
    else return type.getName() + " " + name;
  }

  private CtClass type;
  private String name;
  private Expression val;
}

