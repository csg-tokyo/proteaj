package proteaj.ir;

import javassist.*;

public class IRDefaultArgument {
  public IRDefaultArgument(CtMethod ctMethod, String source, int line) {
    this.ctMethod = ctMethod;
    this.source = source;
    this.line = line;
  }

  public final int line;
  public final String source;
  public final CtMethod ctMethod;
}
