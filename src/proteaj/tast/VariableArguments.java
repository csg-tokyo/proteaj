package proteaj.tast;

import java.util.*;
import javassist.*;

public class VariableArguments extends Arguments {
  public VariableArguments(List<Expression> args, int nargs, CtClass componentType) {
    super(args);
    this.nargs = nargs;
    this.componentType = componentType;
  }

  public final int nargs;
  public final CtClass componentType;
}
