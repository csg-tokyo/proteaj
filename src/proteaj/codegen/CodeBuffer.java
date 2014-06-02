package proteaj.codegen;

import javassist.CtClass;

public class CodeBuffer {
  public CodeBuffer () {
    this.buf = new StringBuilder();
    this.indent = 0;
  }

  public CodeBuffer newLine() {
    buf.append('\n');
    for (int i = 0; i < indent; i++) buf.append("  ");
    return this;
  }

  public CodeBuffer indent() {
    indent++;
    return this;
  }

  public CodeBuffer unindent() {
    indent--;
    return this;
  }

  public CodeBuffer append(boolean bool) {
    buf.append(bool);
    return this;
  }

  public CodeBuffer append(char ch) {
    buf.append(ch);
    return this;
  }

  public CodeBuffer append(int value) {
    buf.append(value);
    return this;
  }

  public CodeBuffer append(long value) {
    buf.append(value);
    return this;
  }

  public CodeBuffer append(float value) {
    buf.append(value);
    return this;
  }

  public CodeBuffer append(double value) {
    buf.append(value);
    return this;
  }

  public CodeBuffer append(String code) {
    buf.append(code);
    return this;
  }

  public CodeBuffer append(CtClass clazz) {
    buf.append(clazz.getName().replace('$', '.'));
    return this;
  }

  public String toString() {
    return buf.toString();
  }

  private StringBuilder buf;
  private int indent;
}
