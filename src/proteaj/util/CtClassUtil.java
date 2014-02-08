package proteaj.util;

import java.util.*;
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

  public static List<CtMethod> getMethods(CtClass cls) {
    if(cache.containsKey(cls)) return cache.get(cls);

    List<CtMethod> methods = new ArrayList<CtMethod>(Arrays.asList(cls.getDeclaredMethods()));

    for(CtMethod method : cls.getMethods()) {
      if(! methods.contains(method)) methods.add(method);
    }

    cache.put(cls, methods);

    return methods;
  }

  private static Map<CtClass, List<CtMethod>> cache = new HashMap<CtClass, List<CtMethod>>();
}

