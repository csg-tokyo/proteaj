package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;

public class ParamAccess extends Expression {
  public ParamAccess(String name, CtClass type, int index) {
    super(type);
    this.name = name;
    this.index = index;
  }

  @Override
  public String toJavassistCode() {
    return "$" + (index + 1);
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final String name;
  public final int index;
}

