package proteaj.ir;

import proteaj.ir.tast.*;

import javassist.*;

public class IRConstructorBody {
  public IRConstructorBody(CtConstructor smethod, String[] paramNames, String source, int line) {
    this.smethod = smethod;
    this.paramNames = paramNames;
    this.source = source;
    this.line = line;
    this.body = null;
  }

  public boolean hasAST() {
    return body != null;
  }

  public void setAST(ConstructorBody body) {
    this.body = body;
  }

  public CtConstructor getCtConstructor() {
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

  public ConstructorBody getAST() {
    return body;
  }

  private int line;
  private String source;
  private String[] paramNames;
  private CtConstructor smethod;
  private ConstructorBody body;
}

