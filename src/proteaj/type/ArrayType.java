package proteaj.type;

import java.util.*;

public class ArrayType implements Type {
  public static ArrayType get (Type componentType) {
    if (! types.containsKey(componentType)) types.put(componentType, new ArrayType(componentType));
    return types.get(componentType);
  }

  @Override
  public String getName() {
    return componentType.getName() + "[]";
  }

  public final Type componentType;

  private ArrayType(Type componentType) {
    this.componentType = componentType;
  }

  private static Map<Type, ArrayType> types = new HashMap<>();
}
