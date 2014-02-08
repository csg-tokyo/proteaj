package proteaj.ir;

import proteaj.ir.tast.*;

import javassist.*;

public class IRFieldBody {
  public IRFieldBody(CtField field, String source, int line) {
    this.field = field;
    this.source = source;
    this.line = line;
    this.body = null;
  }

  public boolean hasAST() {
    return body != null;
  }

  public void setAST(FieldBody body) {
    this.body = body;
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

  public FieldBody getAST() {
    return body;
  }

  private CtField field;
  private String source;
  private int line;
  private FieldBody body;
}
