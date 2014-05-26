package proteaj.type;

import proteaj.error.NotFoundError;

import java.util.*;
import javassist.*;

public abstract class TypeResolver {
  public static RootTypeResolver root () { return RootTypeResolver.getInstance(); }

  public static TypeResolver onFile (String fileName, String packageName, List<String> importPackages, List<String> importClasses) {
    return new TypeResolver_File(fileName, packageName, importPackages, importClasses);
  }

  public static TypeResolver onModule(TypeResolver resolver) {
    return new TypeResolver_Module(resolver);
  }

  public CtClass getType (String name) throws NotFoundError {
    CtClass clazz = getTypeOrNull(name);
    if (clazz == null) throw makeError(name);
    else return clazz;
  }

  public boolean isTypeName (String name) {
    return getTypeOrNull(name) != null;
  }

  public CtClass getTypeOrNull (String name) {
    if (name.endsWith("[]")) {
      int pos = name.indexOf('[');
      String cName = name.substring(0, pos);
      int dim = name.substring(pos).length() / 2;

      CtClass component = getTypeNameOrNull(cName);
      if (component == null) return null;
      else return getArrayType(component, dim);
    }
    else return getTypeNameOrNull(name);
  }

  public CtClass getArrayType (CtClass component, int dim) {
    return getRootResolver().getArrayType(component, dim);
  }

  public RootTypeResolver getRootResolver () {
    return RootTypeResolver.getInstance();
  }

  protected CtClass getTypeNameOrNull (String name) {
    if (cache.containsKey(name)) return cache.get(name);
    CtClass clazz = getTypeNameOrNull_NonCached(name);
    cache.put(name, clazz);
    return clazz;
  }

  protected abstract CtClass getTypeNameOrNull_NonCached (String name);

  protected abstract NotFoundError makeError (String name);

  private Map<String, CtClass> cache = new HashMap<>();
}
