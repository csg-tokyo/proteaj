package proteaj.tast;

import javassist.CtConstructor;

public class ConstructorDeclaration {
  public ConstructorDeclaration (CtConstructor constructor, ConstructorBody body) {
    this.constructor = constructor;
    this.body = body;
  }

  public final CtConstructor constructor;
  public final ConstructorBody body;
}
