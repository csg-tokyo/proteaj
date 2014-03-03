package proteaj.ir;

import proteaj.util.*;

import javassist.*;

public class IROperandAttribute {
  public IROperandAttribute(int mod) {
    this.mod = mod;
    this.sep = null;
    this.defaultMethod = null;
  }

  public IROperandAttribute(int mod, String sep) {
    this.mod = mod;
    this.sep = sep;
    this.defaultMethod = null;
  }

  public void setDefaultMethod(CtMethod defaultMethod) {
    this.defaultMethod = defaultMethod;
  }

  public boolean isReadas() {
    return Modifiers.isReadas(mod);
  }

  public boolean isLazy() {
    return Modifiers.isLazy(mod);
  }

  public boolean isOption() {
    return Modifiers.isOption(mod);
  }

  public boolean hasVarArgs() {
    return Modifiers.hasVarArgs(mod);
  }

  public boolean hasMoreThanOneArgs() {
    return Modifiers.hasMoreThanOneArgs(mod);
  }

  public boolean hasSeparator() {
    return sep != null;
  }

  public int getModifier() {
    return mod;
  }

  public String getSeparator() {
    return sep;
  }

  public CtMethod getDefaultMethod() {
    if(isOption()) {
      assert defaultMethod != null;
      return defaultMethod;
    }
    else return null;
  }

  private int mod;
  private String sep;
  private CtMethod defaultMethod;
}

