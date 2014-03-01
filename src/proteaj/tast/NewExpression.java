package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class NewExpression extends Expression {
  public NewExpression(CtConstructor constructor, List<Expression> args) {
    super(constructor.getDeclaringClass());
    this.constructor = constructor;
    this.args = args;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtConstructor constructor;
  public final List<Expression> args;
}
