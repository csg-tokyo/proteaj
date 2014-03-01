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

  private List<Expression> args;
}
