package proteaj.ir;

import javassist.*;

public class IRStaticInitializer {
  public IRStaticInitializer(CtConstructor clinit, String source, int line) {
    this.clinit = clinit;
    this.source = source;
    this.line = line;
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

  private int line;
  private String source;
  private CtConstructor clinit;
}
