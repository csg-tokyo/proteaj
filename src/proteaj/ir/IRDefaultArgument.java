package proteaj.ir;

import javassist.*;

public class IRDefaultArgument {
  public IRDefaultArgument(CtMethod method, String source, int line) {
    this.method = method;
    this.source = source;
    this.line = line;
  }

  public CtMethod getCtMethod() {
    return method;
  }

  public int getLine() {
    return line;
  }

  public String getSource() {
    return source;
  }

  private int line;
  private String source;
  private CtMethod method;
}
