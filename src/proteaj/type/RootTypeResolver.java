package proteaj.type;

import proteaj.error.NotFoundError;

import javassist.*;

public class RootTypeResolver extends TypeResolver {

  public CtClass makeClass (int modifiers, String name) {
    CtClass clazz = pool.makeClass(name);
    clazz.setModifiers(modifiers);
    return clazz;
  }

  public static RootTypeResolver getInstance() {
    if (instance == null) instance = new RootTypeResolver();
    return instance;
  }

  protected CtClass searchType (String pack, String name) {
    return searchType_FullQualified(pack + '.' + name.replace('.', '$'));
  }

  @Override
  protected CtClass getTypeNameOrNull_NonCached(String name) {
    if (name == null) return null;

    switch (name) {
      case "boolean" : return CtClass.booleanType;
      case "byte"    : return CtClass.byteType;
      case "char"    : return CtClass.charType;
      case "short"   : return CtClass.shortType;
      case "int"     : return CtClass.intType;
      case "long"    : return CtClass.longType;
      case "float"   : return CtClass.floatType;
      case "double"  : return CtClass.doubleType;
      case "void"    : return CtClass.voidType;
    }

    CtClass clazz = searchType("java.lang", name);
    if (clazz != null) return clazz;

    clazz = searchType("proteaj.lang", name);
    if (clazz != null) return clazz;

    clazz = searchType_FullQualified(name);
    if (clazz != null) return clazz;

    return null;
  }

  protected CtClass getArrayType (CtClass clazz, int dim) {
    if (clazz == null) return null;

    StringBuilder buf = new StringBuilder(clazz.getName());
    for (int i = 0; i < dim; i++) buf.append("[]");

    CtClass array = pool.getOrNull(buf.toString());
    assert array != null;
    return array;
  }

  private CtClass searchType_FullQualified(String name) {
    CtClass clazz = pool.getOrNull(name);
    if (clazz != null) return clazz;

    while (name.contains(".")) {
      int index = name.lastIndexOf(".");
      name = name.substring(0, index) + "$" + name.substring(index + 1);
      clazz = pool.getOrNull(name);
      if (clazz != null) return clazz;
    }

    return pool.getOrNull(name);
  }

  @Override
  protected NotFoundError makeError(String name) {
    return new NotFoundError(name + " is not found", "(no source)", 0);
  }

  private RootTypeResolver () {
    this.pool = ClassPool.getDefault();
  }

  private final ClassPool pool;

  private static RootTypeResolver instance = null;
}
