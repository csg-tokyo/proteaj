package proteaj.ir;

import javassist.*;

public class IRConstructor {
  public IRConstructor(CtConstructor ctConstructor, String[] paramNames, String source, int line) {
    this.ctConstructor = ctConstructor;
    this.paramNames = paramNames;
    this.source = source;
    this.line = line;
  }

  public CtClass[] getParamTypes() throws NotFoundException {
    return ctConstructor.getParameterTypes();
  }

  public final int line;
  public final String source;
  public final String[] paramNames;
  public final CtConstructor ctConstructor;
}

