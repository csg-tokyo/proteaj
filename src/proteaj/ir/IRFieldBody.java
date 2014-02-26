package proteaj.ir;

import javassist.*;

public class IRFieldBody {
  public IRFieldBody(CtField field, String source, int line) {
    this.field = field;
    this.source = source;
    this.line = line;
  }

  public CtField getCtField() {
    return field;
  }

  public String getSource() {
    return source;
  }

  public int getLine() {
    return line;
  }

  private CtField field;
  private String source;
  private int line;
}
