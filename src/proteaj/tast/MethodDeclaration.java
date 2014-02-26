package proteaj.tast;

import javassist.CtMethod;

public class MethodDeclaration {
  public MethodDeclaration (CtMethod method, MethodBody body) {
    this.method = method;
    this.body = body;
  }

  public final CtMethod method;
  public final MethodBody body;
}
