package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class ExpressionList extends Expression {
  public ExpressionList(List<Expression> exprs) {
    super(CtClass.voidType);
    this.exprs = exprs;
  }

  @Override
  public String toJavassistCode() {
    if(exprs.isEmpty()) return "";

    StringBuilder buf = new StringBuilder();

    buf.append(exprs.get(0).toJavassistCode());
    for(int i = 1; i < exprs.size(); i++) {
      buf.append(',').append(exprs.get(i).toJavassistCode());
    }

    return buf.toString();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> exprs;
}

