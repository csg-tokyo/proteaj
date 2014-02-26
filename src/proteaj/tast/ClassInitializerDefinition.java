package proteaj.tast;

import javassist.CtConstructor;

public class ClassInitializerDefinition {
  public ClassInitializerDefinition(CtConstructor clIni, ClassInitializer body) {
    this.clIni = clIni;
    this.body = body;
  }

  public final CtConstructor clIni;
  public final ClassInitializer body;
}
