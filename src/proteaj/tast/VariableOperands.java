package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class VariableOperands extends Expression {
  public VariableOperands(List<Expression> args, CtClass arrayType) {
    super(arrayType);
    this.args = args;
  }

  @Override
  public String toJavassistCode() {
    if(args.isEmpty()) try {
      return "new " + getType().getComponentType().getName() + "[0]";
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }

    StringBuilder buf = new StringBuilder();

    buf.append("new ").append(getType().getName()).append('{');


    buf.append(args.get(0).toJavassistCode());

    for(int i = 1; i < args.size(); i++) {
      buf.append(',').append(args.get(i).toJavassistCode());
    }

    buf.append('}');

    return buf.toString();
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final List<Expression> args;
}

