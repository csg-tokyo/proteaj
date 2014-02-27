package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;

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

  @Override
  public String toJavassistCode() {
    if(val != null) return type.getName() + " " + name + " = " + val.toJavassistCode();
    else return type.getName() + " " + name;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass type;
  public final String name;
  public final Expression val;
}

