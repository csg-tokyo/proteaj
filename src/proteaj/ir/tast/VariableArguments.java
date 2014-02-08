package proteaj.ir.tast;

import java.util.*;
import javassist.*;

public class VariableArguments extends Arguments {
  public VariableArguments(List<Expression> args, int nargs, CtClass componentType) {
    super(args);
    this.nargs = nargs;
    this.componentType = componentType;
  }

  @Override
  public String toJavassistCode() {
    assert nargs != 0;

    List<Expression> args = getArgs();

    StringBuilder buf = new StringBuilder();
    buf.append('(');

    for(int i = 0; i < nargs - 1; i++) {
      buf.append(args.get(i).toJavassistCode()).append(',');
    }

    if(args.size() == nargs - 1) {
      buf.append("new ").append(componentType.getName()).append("[0]").append(')');
      return buf.toString();
    }

    buf.append("new ").append(componentType.getName()).append("[]").append('{');

    buf.append(args.get(nargs - 1).toJavassistCode());

    for(int i = nargs; i < args.size(); i++) {
      buf.append(',').append(args.get(i).toJavassistCode());
    }

    buf.append('}').append(')');

    return buf.toString();
  }

  private int nargs;
  private CtClass componentType;
}
