package proteaj.util;

import proteaj.error.*;

import java.util.*;
import javassist.*;
import javassist.bytecode.*;

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

  public static List<CtMethod> getMethods(CtClass cls) {
    if(cache.containsKey(cls)) return cache.get(cls);

    List<CtMethod> methods = new ArrayList<CtMethod>(Arrays.asList(cls.getDeclaredMethods()));

    for(CtMethod method : cls.getMethods()) {
      if(! methods.contains(method)) methods.add(method);
    }

    cache.put(cls, methods);

    return methods;
  }

  // not perfect
  public static boolean isCastable (CtClass from, CtClass to, String file, int line) {
    return from.isPrimitive() || to.isPrimitive() || isSubtype(from, to, file, line) || isSubtype(to, from, file, line) || to == CtClass.voidType;
  }

  public static boolean isSubtype (CtClass sub, CtClass sup, String file, int line) {
    try {
      return sub.subtypeOf(sup);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, file, line));
      return false;
    }
  }

  public static boolean hasTypeParameter (CtClass clazz, String file, int line) {
    SignatureAttribute sig = (SignatureAttribute)clazz.getClassFile2().getAttribute("Signature");
    if (sig == null) return false;

    try {
      SignatureAttribute.ClassSignature s = SignatureAttribute.toClassSignature(sig.getSignature());
      return s.getParameters().length != 0;
    } catch (BadBytecode e) {
      ErrorList.addError(new BadBytecodeError(e, file, line));
      return false;
    }
  }

  private static Map<CtClass, List<CtMethod>> cache = new HashMap<CtClass, List<CtMethod>>();
}

