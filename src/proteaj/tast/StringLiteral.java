package proteaj.tast;

import proteaj.ir.*;
import proteaj.tast.util.ExpressionVisitor;

import static proteaj.util.Escape.*;

public class StringLiteral extends Expression {
  public StringLiteral(String str) {
    super(IRCommonTypes.getStringType());
    this.str = str;
  }

  @Override
  public String toJavassistCode() {
    return '"' + escape(str) + '"';
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  @Override
  public String toString() {
    return "\"" + str + "\"";
  }

  public final String str;
}

