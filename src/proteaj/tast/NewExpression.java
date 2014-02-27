package proteaj.tast;

import javassist.CtConstructor;
import proteaj.tast.util.ExpressionVisitor;

public class NewExpression extends Expression {
  public NewExpression(CtConstructor constructor, Arguments args) {
    super(constructor.getDeclaringClass());
    this.constructor = constructor;
    this.args = args;
  }

  @Override
  public String toJavassistCode() {
    return "new " + constructor.getDeclaringClass().getName() + args.toJavassistCode();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtConstructor constructor;
  public final Arguments args;
}
