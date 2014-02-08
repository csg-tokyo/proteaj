package proteaj.ir;

import proteaj.ir.tast.*;

import javassist.*;

public class IRStaticInitializer {
  public IRStaticInitializer(CtConstructor clinit, String source, int line) {
    this.clinit = clinit;
    this.source = source;
    this.line = line;
    this.body = null;
  }

  public boolean hasAST() {
    return body != null;
  }

  public void setAST(ClassInitializer body) {
    this.body = body;
  }

  public CtConstructor getCtConstructor() {
    return clinit;
  }

  public int getLine() {
    return line;
  }

  public String getSource() {
    return source;
  }

  public ClassInitializer getAST() {
    return body;
  }

  private int line;
  private String source;
  private CtConstructor clinit;
  private ClassInitializer body;
}
