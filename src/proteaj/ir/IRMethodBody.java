package proteaj.ir;

import javassist.*;

public class IRMethodBody {
  public IRMethodBody(CtMethod smethod, String[] paramNames, String source, int line) {
    this.smethod = smethod;
    this.paramNames = paramNames;
    this.source = source;
    this.line = line;
  }

  public CtMethod getCtMethod() {
    return smethod;
  }

  public CtClass[] getParamTypes() throws NotFoundException {
    return smethod.getParameterTypes();
  }

  public String[] getParamNames() {
    return paramNames;
  }

  public int getLine() {
    return line;
  }

  public String getSource() {
    return source;
  }

  private int line;
  private String source;
  private String[] paramNames;
  private CtMethod smethod;
}

