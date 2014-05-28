package proteaj.env.type;

import javassist.CtClass;
import proteaj.error.NotFoundError;

class TypeResolver_Module extends TypeResolver {
  public TypeResolver_Module(TypeResolver parent) {
    this.parent = parent;
  }

  @Override
  protected CtClass getTypeNameOrNull_NonCached(String name) {
    return parent.getTypeNameOrNull(name);
  }

  @Override
  protected NotFoundError makeError(String name) {
    return parent.makeError(name);
  }

  private final TypeResolver parent;
}
