package proteaj.type;

import proteaj.error.NotFoundError;
import proteaj.ir.IRHeader;

import java.util.*;
import javassist.*;

public class TypeResolver {
  public TypeResolver (IRHeader header, ClassPool pool) {
    this.pool = pool;
    this.header = header;
    this.cache = new HashMap<>();
  }

  public Type getTypeOrNull (String name) {
    if (name.endsWith("[]")) {
      Type componentType = getTypeOrNull(name.substring(0, name.length() - 2));
      if (componentType == null) return null;
      else return getArrayType(componentType);
    }
    else return getSimpleTypeOrNull(name);
  }

  public Type getType (String name) throws NotFoundError {
    if (name.endsWith("[]")) return getArrayType(getType(name.substring(0, name.length() - 2)));
    else return getSimpleType(name);
  }

  public Type getSimpleTypeOrNull (String name) {
    switch (name) {
      case "boolean" : return PrimitiveType.booleanType;
      case "byte"    : return PrimitiveType.byteType;
      case "char"    : return PrimitiveType.charType;
      case "double"  : return PrimitiveType.doubleType;
      case "float"   : return PrimitiveType.floatType;
      case "int"     : return PrimitiveType.intType;
      case "long"    : return PrimitiveType.longType;
      case "short"   : return PrimitiveType.shortType;
      case "void"    : return PrimitiveType.voidType;
    }

    if (cache.containsKey(name)) return cache.get(name);

    if (header.containsAbbName(name)) {
      CtClass clazz = pool.getOrNull(header.getLongName(name));
      if (clazz != null) {
        Type type = RawType.get(clazz);
        cache.put(name, type);
        return type;
      }
      else return null;
    }

    if (! header.packageName.isEmpty()) {
      CtClass clazz = pool.getOrNull(header.packageName + '.' + name);
      if (clazz != null) {
        header.addAbbName(name, clazz.getName());
        Type type = RawType.get(clazz);
        cache.put(name, type);
        return type;
      }
    }

    CtClass clazz = pool.getOrNull(name);
    if (clazz != null) {
      Type type = RawType.get(clazz);
      cache.put(name, type);
      return type;
    }

    for (String pack : header.importPackages) {
      clazz = pool.getOrNull(pack + '.' + name);
      if (clazz != null) {
        header.addAbbName(name, clazz.getName());
        Type type = RawType.get(clazz);
        cache.put(name, type);
        return type;
      }
    }

    return null;
  }

  public Type getSimpleType (String name) throws NotFoundError {
    Type type = getSimpleTypeOrNull(name);
    if (type == null) throw new NotFoundError("class " + name + " is not found", header.filePath, 0);
    else return type;
  }

  public ArrayType getArrayType (Type type) {
    return ArrayType.get(type);
  }

  public ArrayType getArrayType (Type type, int dim) {
    if (dim > 1) return getArrayType(ArrayType.get(type), dim - 1);
    else return ArrayType.get(type);
  }

  private final ClassPool pool;
  private final IRHeader header;
  private Map<String, Type> cache;
}
