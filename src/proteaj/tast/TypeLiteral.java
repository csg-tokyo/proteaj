package proteaj.tast;

import javassist.CtClass;
import proteaj.tast.util.ExpressionVisitor;
import proteaj.type.CommonTypes;

public class TypeLiteral  extends Expression {
  public TypeLiteral(CtClass cls) {
    super(CommonTypes.getInstance().typeType);
    this.cls = cls;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass cls;
}