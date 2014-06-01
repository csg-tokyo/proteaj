package proteaj.util;

import javassist.*;

public class CtClassUtil {
  public static CtConstructor getConstructor(CtClass cls, CtClass... argTypes) throws NotFoundException {
    return cls.getDeclaredConstructor(argTypes);
  }

  public static boolean hasDefaultConstructor(CtClass cls) {
    for(CtConstructor c : cls.getDeclaredConstructors()) try {
      if(c.getParameterTypes().length == 0) return true;
    } catch (NotFoundException e) {}
    return false;
  }

  public static CtConstructor getDefaultConstructor(CtClass cls) throws NotFoundException {
    return cls.getDeclaredConstructor(new CtClass[0]);
  }

  // not perfect
  public static boolean isCastable (CtClass from, CtClass to) throws NotFoundException {
    return from.isPrimitive() || to.isPrimitive()
        || from.isInterface() || to.isInterface()
        || from.subtypeOf(to) || to.subtypeOf(from) || to == CtClass.voidType;
  }

  public static boolean isAssignable (CtClass from, CtClass to) throws NotFoundException {
    if (to == CtClass.voidType) return true;
    if (from.isPrimitive()) return isAssignable_Primitive(from, to);
    else return from.subtypeOf(to);
  }

  public static boolean isSubtype (CtClass from, CtClass to) throws NotFoundException {
    if (from.isPrimitive()) return isAssignable_Primitive(from, to);
    else return from.subtypeOf(to);
  }

  private static boolean isAssignable_Primitive (CtClass from, CtClass to) {
    if (from == to) return true;
    if (from == CtClass.byteType) return to == CtClass.shortType || to == CtClass.intType || to == CtClass.longType || to == CtClass.floatType || to == CtClass.doubleType;
    if (from == CtClass.charType) return to == CtClass.intType || to == CtClass.longType || to == CtClass.floatType || to == CtClass.doubleType;
    if (from == CtClass.shortType) return to == CtClass.intType || to == CtClass.longType || to == CtClass.floatType || to == CtClass.doubleType;
    if (from == CtClass.intType) return to == CtClass.longType || to == CtClass.floatType || to == CtClass.doubleType;
    if (from == CtClass.longType) return to == CtClass.floatType || to == CtClass.doubleType;
    if (from == CtClass.floatType) return to == CtClass.doubleType;
    return false;
  }

}

