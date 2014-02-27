package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class NewArrayExpression extends Expression {
  public NewArrayExpression(CtClass arrayType, List<Expression> args) {
    super(arrayType);
    this.args = args;
  }

  @Override
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();
    buf.append("new ");

    int dim = 0;
    CtClass t = getType();
    while(t.isArray()) try {
      t = t.getComponentType();
      dim++;
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException("invalid array type");
    }

    buf.append(t.getName());

    for(Expression arg : args) {
      buf.append('[').append(arg.toJavassistCode()).append(']');
      dim--;
    }

    while(dim > 0) {
      buf.append("[]");
      dim--;
    }

    return buf.toString();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> args;
}

