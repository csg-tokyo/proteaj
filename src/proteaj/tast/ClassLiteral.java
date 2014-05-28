package proteaj.tast;

import javassist.*;
import proteaj.tast.util.ExpressionVisitor;
import proteaj.env.type.CommonTypes;

public class ClassLiteral extends Expression {
  public ClassLiteral(CtClass cls) {
    super(CommonTypes.getInstance().classType);
    this.cls = cls;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass cls;
}
