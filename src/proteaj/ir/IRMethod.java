package proteaj.ir;

import javassist.*;

public class IRMethod {
  public IRMethod(CtMethod ctMethod, String[] paramNames, String source, int line) {
    this.ctMethod = ctMethod;
    this.paramNames = paramNames;
    this.source = source;
    this.line = line;
  }

  public CtClass[] getParamTypes() throws NotFoundException {
    return ctMethod.getParameterTypes();
  }

  public final int line;
  public final String source;
  public final String[] paramNames;
  public final CtMethod ctMethod;
}

