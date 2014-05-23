package proteaj.ir;

import javassist.*;

public class IRField {
  public IRField(CtField ctField, String source, int line) {
    this.ctField = ctField;
    this.source = source;
    this.line = line;
  }

  public final CtField ctField;
  public final String source;
  public final int line;
}
