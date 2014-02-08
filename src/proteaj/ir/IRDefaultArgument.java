package proteaj.ir;

import proteaj.ir.tast.*;

import javassist.*;

public class IRDefaultArgument {
  public IRDefaultArgument(CtMethod method, String source, int line) {
    this.method = method;
    this.source = source;
    this.line = line;
    this.body = null;
  }

  public boolean hasAST() {
    return body != null;
  }

  public void setAST(DefaultValue body) {
    this.body = body;
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

  public DefaultValue getAST() {
    return body;
  }

  private int line;
  private String source;
  private CtMethod method;
  private DefaultValue body;
}

