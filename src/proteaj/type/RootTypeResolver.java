package proteaj.type;

import proteaj.error.*;
import proteaj.util.Modifiers;

import java.util.*;
import javassist.*;
import javassist.bytecode.*;

public class RootTypeResolver extends TypeResolver {

  public CtClass makeClass (String name) {
    CtClass clazz = pool.makeClass(name);
    return clazz;
  }

  public CtClass makeClass (int modifiers, String name) {
    CtClass clazz = pool.makeClass(name);
    clazz.setModifiers(modifiers);
    return clazz;
  }

  public CtClass makeInterface (int modifiers, String name) {
    CtClass clazz = pool.makeInterface(name);
    clazz.setModifiers(clazz.getModifiers() | modifiers);
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

  public CtClass getArrayType (CtClass clazz, int dim) {
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





/*
  public CtClass getParameterizedClass(CtClass genericClass, List<CtClass> args) throws SemanticsError, NotFoundError {
    if (args.isEmpty()) return genericClass;

    CtClass fromCache = getParameterizedClassFromCache(genericClass, args);
    if (fromCache != null) return fromCache;

    SignatureAttribute.ClassSignature s = getClassSignature(genericClass);
    if (s == null) throw new SemanticsError(genericClass.getName() + " is not generic class", hdata.filePath, 0);

    SignatureAttribute.TypeParameter[] params = s.getParameters();
    if (params.length != args.size()) throw new SemanticsError("(# of params) = " + params.length + ", but (# of args) = " + args.size(), hdata.filePath, 0);

    StringBuilder name = new StringBuilder();
    name.append(genericClass.getName()).append('<');

    Map<String, CtClass> map = new HashMap<>();
    for (int i = 0; i < params.length; i++) {
      CtClass arg = args.get(i);

      // TODO : check bounds
      if (i != 0) name.append(',');
      name.append(arg.getSimpleName());
      map.put(params[i].getName(), arg);
    }
    name.append('>');

    CtClass clazz = pool.makeClass(name.toString());
    putParameterizedClassToCache(genericClass, args, clazz);

    CtClass sup = classTypeToCtClass(s.getSuperClass(), clazz, map);
    CtClass[] interfaces = new CtClass[s.getInterfaces().length];
    for (int i = 0; i < interfaces.length; i++) interfaces[i] = classTypeToCtClass(s.getInterfaces()[i], clazz, map);

    try { clazz.setSuperclass(sup); } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }
    clazz.setInterfaces(interfaces);
    clazz.setModifiers(genericClass.getModifiers());

    for (CtConstructor constructor : genericClass.getConstructors()) try {
      if (constructor.isClassInitializer()) continue;
      clazz.addConstructor(getParameterizedConstructor(constructor, clazz, map));
    } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }

    for (CtMethod method : genericClass.getDeclaredMethods()) try {
      if (Modifiers.isStatic(method)) continue;
      clazz.addMethod(getParameterizedMethod(method, clazz, map));
    } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }

    for (CtField field : genericClass.getDeclaredFields()) try {
      if (Modifiers.isStatic(field)) continue;
      clazz.addField(getParameterizedField(field, clazz, map));
    } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }

    return clazz;
  }

  private SignatureAttribute.ClassSignature getClassSignature (CtClass clazz) {
    try {
      return SignatureAttribute.toClassSignature(clazz.getGenericSignature());
    } catch (BadBytecode e) {
      ErrorList.addError(new BadBytecodeError(e, hdata.filePath, 0));
      return null;
    }
  }

  private SignatureAttribute.MethodSignature getMethodSignature (CtBehavior behavior) {
    String sig = behavior.getGenericSignature();
    if (sig != null) try {
      return SignatureAttribute.toMethodSignature(sig);
    } catch (BadBytecode e) {
      ErrorList.addError(new BadBytecodeError(e, hdata.filePath, 0));
    }
    return null;
  }

  private SignatureAttribute.ObjectType getFieldSignature (CtField field) {
    String sig = field.getGenericSignature();
    if (sig != null) try {
      return SignatureAttribute.toFieldSignature(sig);
    } catch (BadBytecode e) {
      ErrorList.addError(new BadBytecodeError(e, hdata.filePath, 0));
    }
    return null;
  }

  private CtConstructor getParameterizedConstructor (CtConstructor constructor, CtClass clazz, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    SignatureAttribute.MethodSignature mSig = getMethodSignature(constructor);
    if (mSig == null) return cloneConstructor(constructor, clazz);

    SignatureAttribute.Type[] pts = mSig.getParameterTypes();
    SignatureAttribute.ObjectType[] ets = mSig.getExceptionTypes();
    SignatureAttribute.TypeParameter[] tps = mSig.getTypeParameters();

    CtClass[] paramTypes = new CtClass[pts.length];
    for (int i = 0; i < pts.length; i++) paramTypes[i] = typeToCtClass(pts[i], clazz, map);
    CtClass[] exceptions = new CtClass[ets.length];
    for (int i = 0; i < ets.length; i++) exceptions[i] = objectTypeToCtClass(ets[i], clazz, map);

    CtConstructor c = new CtConstructor(paramTypes, clazz);
    try { c.setExceptionTypes(exceptions); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    c.setModifiers(constructor.getModifiers());

    if (tps.length > 0) {
      SignatureAttribute.Type ret = ctRefClassToType(clazz);
      SignatureAttribute.Type[] sigParams = new SignatureAttribute.Type[pts.length];
      SignatureAttribute.ObjectType[] sigThrows = new SignatureAttribute.ObjectType[ets.length];

      for (int i = 0; i < pts.length; i++) sigParams[i] = replaceTypeVars(pts[i], map);
      for (int i = 0; i < ets.length; i++) sigThrows[i] = replaceTypeVars_Object(ets[i], map);

      c.setGenericSignature(new SignatureAttribute.MethodSignature(tps, sigParams, ret, sigThrows).encode());
    }

    return c;
  }

  private CtMethod getParameterizedMethod (CtMethod method, CtClass clazz, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    SignatureAttribute.MethodSignature mSig = getMethodSignature(method);
    if (mSig == null) return cloneMethod(method, clazz);

    SignatureAttribute.Type[] pts = mSig.getParameterTypes();
    SignatureAttribute.ObjectType[] ets = mSig.getExceptionTypes();
    SignatureAttribute.TypeParameter[] tps = mSig.getTypeParameters();

    CtClass returnType = typeToCtClass(mSig.getReturnType(), clazz, map);
    CtClass[] paramTypes = new CtClass[pts.length];
    for (int i = 0; i < pts.length; i++) paramTypes[i] = typeToCtClass(pts[i], clazz, map);
    CtClass[] exceptions = new CtClass[ets.length];
    for (int i = 0; i < ets.length; i++) exceptions[i] = objectTypeToCtClass(ets[i], clazz, map);

    CtMethod m = new CtMethod(returnType, method.getName(), paramTypes, clazz);
    try { m.setExceptionTypes(exceptions); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    m.setModifiers(method.getModifiers() | Modifiers.ABSTRACT);  // javassist does not support non-abstract interface method. [ad-hoc solution]

    if (tps.length > 0) {
      SignatureAttribute.Type ret = ctRefClassToType(clazz);
      SignatureAttribute.Type[] sigParams = new SignatureAttribute.Type[pts.length];
      SignatureAttribute.ObjectType[] sigThrows = new SignatureAttribute.ObjectType[ets.length];

      for (int i = 0; i < pts.length; i++) sigParams[i] = replaceTypeVars(pts[i], map);
      for (int i = 0; i < ets.length; i++) sigThrows[i] = replaceTypeVars_Object(ets[i], map);

      m.setGenericSignature(new SignatureAttribute.MethodSignature(tps, sigParams, ret, sigThrows).encode());
    }

    return m;
  }

  private CtField getParameterizedField (CtField field, CtClass clazz, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    SignatureAttribute.ObjectType fSig = getFieldSignature(field);
    if (fSig == null) return cloneField(field, clazz);

    CtClass fieldType = objectTypeToCtClass(fSig, clazz, map);
    final CtField f;
    try { f = new CtField(fieldType, field.getName(), clazz); } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }
    f.setModifiers(field.getModifiers());
    return f;
  }

  private CtConstructor cloneConstructor (CtConstructor constructor, CtClass clazz) throws NotFoundError {
    final CtConstructor c;
    try { c = new CtConstructor(constructor.getParameterTypes(), clazz); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    try { c.setExceptionTypes(constructor.getExceptionTypes()); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    c.setModifiers(constructor.getModifiers());
    return c;
  }

  private CtMethod cloneMethod (CtMethod method, CtClass clazz) throws NotFoundError {
    final CtMethod m;
    try { m = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), clazz); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    try { m.setExceptionTypes(method.getExceptionTypes()); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    m.setModifiers(method.getModifiers() | Modifiers.ABSTRACT);  // javassist does not support non-abstract interface method. [ad-hoc solution]
    return m;
  }

  private CtField cloneField (CtField field, CtClass clazz) throws SemanticsError, NotFoundError {
    final CtClass type;
    try { type = field.getType(); } catch (NotFoundException e) {
      throw new NotFoundError(e, hdata.filePath, 0);
    }
    final CtField f;
    try {
      f = new CtField(type, field.getName(), clazz);
    } catch (CannotCompileException e) {
      throw new SemanticsError(e.getMessage(), hdata.filePath, 0);
    }
    f.setModifiers(field.getModifiers());
    return f;
  }

  private CtClass typeToCtClass (SignatureAttribute.Type t, CtClass enclosing, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    if (t instanceof SignatureAttribute.BaseType) return ((SignatureAttribute.BaseType)t).getCtlass();
    else if (t instanceof SignatureAttribute.ObjectType) return objectTypeToCtClass((SignatureAttribute.ObjectType)t, enclosing, map);
    else throw new RuntimeException("invalid type");
  }

  private CtClass objectTypeToCtClass (SignatureAttribute.ObjectType ot, CtClass enclosing, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    if (ot instanceof SignatureAttribute.ClassType) return classTypeToCtClass((SignatureAttribute.ClassType) ot, enclosing, map);
    else if (ot instanceof SignatureAttribute.TypeVariable) return typeVarToCtClass((SignatureAttribute.TypeVariable)ot, map);
    else if (ot instanceof SignatureAttribute.ArrayType) return arrayTypeToCtClass((SignatureAttribute.ArrayType)ot, enclosing, map);
    else throw new RuntimeException("invalid type argument : " + ot);
  }

  private CtClass classTypeToCtClass (SignatureAttribute.ClassType t, CtClass enclosing, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    CtClass clazz = searchType_FullQualified(t.getName());
    if (clazz == null) {
      // ad-hoc
      clazz = searchType_FullQualified(enclosing.getName().substring(0, enclosing.getName().indexOf('<')) + "$" + t.getName());
      if (clazz == null) throw new NotFoundError(t.getName() + " is not found", hdata.filePath, 0);
    }

    SignatureAttribute.TypeArgument[] typeArgs = t.getTypeArguments();
    if (typeArgs == null) return clazz;

    List<CtClass> args = new ArrayList<>();
    for (SignatureAttribute.TypeArgument arg : typeArgs) {
      args.add(typeArgToCtClass(arg, enclosing, map));
    }
    return getParameterizedClass(clazz, args);
  }

  private CtClass typeVarToCtClass (SignatureAttribute.TypeVariable var, Map<String, CtClass> map) {
    if (map.containsKey(var.getName())) return map.get(var.getName());
    else return CommonTypes.getInstance().objectType;
  }

  private CtClass arrayTypeToCtClass (SignatureAttribute.ArrayType array, CtClass enclosing, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    CtClass component = typeToCtClass(array.getComponentType(), enclosing, map);
    return getArrayType(component, array.getDimension());
  }

  private CtClass typeArgToCtClass (SignatureAttribute.TypeArgument arg, CtClass enclosing, Map<String, CtClass> map) throws NotFoundError, SemanticsError {
    if (arg.isWildcard()) return CommonTypes.getInstance().objectType;
    else return objectTypeToCtClass(arg.getType(), enclosing, map);
  }

  private SignatureAttribute.Type ctClassToType (CtClass clazz) throws NotFoundError {
    if (clazz.isPrimitive()) return new SignatureAttribute.BaseType(clazz.getName());
    else return ctRefClassToType(clazz);
  }

  private SignatureAttribute.ObjectType ctRefClassToType (CtClass clazz) throws NotFoundError {
    if (clazz.isArray()) return ctArrayToArrayType(clazz);
    else return new SignatureAttribute.ClassType(clazz.getName());
  }

  private SignatureAttribute.ArrayType ctArrayToArrayType (CtClass clazz) throws NotFoundError {
    int dim = 0;
    while (clazz.isArray()) {
      try { clazz = clazz.getComponentType(); } catch (NotFoundException e) {
        throw new NotFoundError(e, hdata.filePath, 0);
      }
      dim++;
    }
    return new SignatureAttribute.ArrayType(dim, ctClassToType(clazz));
  }

  private SignatureAttribute.Type replaceTypeVars (SignatureAttribute.Type t, Map<String, CtClass> map) throws NotFoundError {
    if (t instanceof SignatureAttribute.BaseType) return t;
    else return replaceTypeVars_Object((SignatureAttribute.ObjectType) t, map);
  }

  private SignatureAttribute.ObjectType replaceTypeVars_Object (SignatureAttribute.ObjectType t, Map<String, CtClass> map) throws NotFoundError {
    if (t instanceof SignatureAttribute.ArrayType) return replaceTypeVars_Array((SignatureAttribute.ArrayType)t, map);
    else if (t instanceof SignatureAttribute.ClassType) return replaceTypeVars_Class((SignatureAttribute.ClassType)t, map);
    else if (t instanceof SignatureAttribute.TypeVariable) return replaceTypeVars_TypeVar((SignatureAttribute.TypeVariable)t, map);
    else throw new RuntimeException("invalid type argument");
  }

  private SignatureAttribute.ArrayType replaceTypeVars_Array (SignatureAttribute.ArrayType t, Map<String, CtClass> map) throws NotFoundError {
    return new SignatureAttribute.ArrayType(t.getDimension(), replaceTypeVars(t.getComponentType(), map));
  }

  private SignatureAttribute.ClassType replaceTypeVars_Class (SignatureAttribute.ClassType t, Map<String, CtClass> map) throws NotFoundError {
    SignatureAttribute.TypeArgument[] args = t.getTypeArguments();
    if (args == null) return t;

    SignatureAttribute.TypeArgument[] as = new SignatureAttribute.TypeArgument[args.length];
    for (int i = 0; i < args.length; i++) as[i] = replaceTypeVars_TypeArg(args[i], map);

    return new SignatureAttribute.ClassType(t.getName(), as);
  }

  private SignatureAttribute.ObjectType replaceTypeVars_TypeVar (SignatureAttribute.TypeVariable t, Map<String, CtClass> map) throws NotFoundError {
    if (! map.containsKey(t.getName())) return t;
    else return ctRefClassToType(map.get(t.getName()));
  }

  private SignatureAttribute.TypeArgument replaceTypeVars_TypeArg (SignatureAttribute.TypeArgument a, Map<String, CtClass> map) throws NotFoundError {
    if (a.isWildcard()) return a;
    else return new SignatureAttribute.TypeArgument(replaceTypeVars_Object(a.getType(), map));
  }

  private CtClass getParameterizedClassFromCache(CtClass genericClass, List<CtClass> args) {
    if (cache.containsKey(genericClass)) {
      Map<List<CtClass>, CtClass> map = cache.get(genericClass);
      if (map.containsKey(args)) return map.get(args);
    }
    return null;
  }

  private void putParameterizedClassToCache(CtClass genericClass, List<CtClass> args, CtClass clazz) {
    if (! cache.containsKey(genericClass)) cache.put(genericClass, new HashMap<>());
    Map<List<CtClass>, CtClass> map = cache.get(genericClass);
    map.put(args, clazz);
  }

  private Map<CtClass, Map<List<CtClass>, CtClass>> cache = new HashMap<>();
*/
}
