package proteaj.tast;

import java.util.*;

public class Arguments {
  public static final Arguments EMPTY_ARGS = new Arguments(Collections.<Expression>emptyList());

  public Arguments(Expression... args) {
    this.args = Arrays.asList(args);
  }

  public Arguments(List<Expression> args) {
    this.args = args;
  }

  public List<Expression> getArgs() {
    return args;
  }

  public String toJavassistCode() {
    if(args.isEmpty()) return "()";

    StringBuilder buf = new StringBuilder();
    buf.append("(");
    buf.append(args.get(0).toJavassistCode());

    for(int i = 1; i < args.size(); i++) {
      buf.append(',').append(args.get(i).toJavassistCode());
    }

    buf.append(')');

    return buf.toString();
  }

  private List<Expression> args;
}
