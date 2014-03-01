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
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> exprs;
}

