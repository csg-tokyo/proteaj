package proteaj.ir;

import javassist.*;

public class IRStaticInitializer {
  public IRStaticInitializer(CtConstructor clInit, String source, int line) {
    this.clInit = clInit;
    this.source = source;
    this.line = line;
  }

  public final int line;
  public final String source;
  public final CtConstructor clInit;
}
