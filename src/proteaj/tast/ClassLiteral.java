package proteaj.tast;

import proteaj.ir.*;

import javassist.*;
import proteaj.tast.util.ExpressionVisitor;

public class ClassLiteral extends Expression {
  public ClassLiteral(CtClass cls) {
    super(IRCommonTypes.getClassType());
    this.cls = cls;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass cls;
}
