package proteaj.type;

import java.util.*;
import javassist.*;

public class RawType implements Type {
  public static RawType get (CtClass clazz) {
    if (! types.containsKey(clazz)) return types.put(clazz, new RawType(clazz));
    return types.get(clazz);
  }

  @Override
  public String getName() {
    return clazz.getName();
  }

  private RawType(CtClass clazz) {
    this.clazz = clazz;
  }

  private final CtClass clazz;
  private static Map<CtClass, RawType> types = new HashMap<>();
}
