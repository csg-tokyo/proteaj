package proteaj.tast;

import javassist.*;
import proteaj.tast.util.ExpressionVisitor;

import static proteaj.util.Escape.*;

public class CharLiteral extends Expression {
  public CharLiteral(char val) {
    super(CtClass.charType);
    this.val = val;
  }

  @Override
  public String toJavassistCode() {
    return "\'" + escape(val) + "\'";
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final char val;
}
