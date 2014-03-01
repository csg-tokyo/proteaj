package proteaj.tast;

import proteaj.tast.util.*;

import javassist.*;

public class CharLiteral extends Expression {
  public CharLiteral(char val) {
    super(CtClass.charType);
    this.val = val;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final char val;
}
