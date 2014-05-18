package proteaj.tast;

import javassist.CtClass;
import proteaj.ir.IRCommonTypes;
import proteaj.tast.util.ExpressionVisitor;

public class TypeLiteral  extends Expression {
  public TypeLiteral(CtClass cls) {
    super(IRCommonTypes.getTypeType());
    this.cls = cls;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass cls;
}