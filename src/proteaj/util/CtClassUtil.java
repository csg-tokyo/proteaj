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

  // not perfect
  public static boolean isCastable (CtClass from, CtClass to) throws NotFoundException {
    return from.isPrimitive() || to.isPrimitive() || from.subtypeOf(to) || to.subtypeOf(from) || to == CtClass.voidType;
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

