package proteaj.ir.tast;

import proteaj.error.FailLog;

public abstract class TypedAST {
  public boolean isFail() { return false; }
  public FailLog getFailLog() { return null; }

  public abstract String toJavassistCode();
}

