package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;

public class LocalVariable extends Expression {
  public LocalVariable(String name, CtClass type) {
    super(type);
    this.name = name;
  }

  @Override
  public String toJavassistCode() {
    return name;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final String name;
}

