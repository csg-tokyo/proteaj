package proteaj.codegen.lazy;

import proteaj.tast.*;
import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class DefinitionTranslator extends TreeTranslator {
  public DefinitionTranslator(Map<Integer, CtMethod> lazyMap) {
    this.lazyMap = lazyMap;
  }

  @Override
  public Expression translate(ParamAccess paramAccess) {
    if (lazyMap.containsKey(paramAccess.index)) try {
      CtMethod method = lazyMap.get(paramAccess.index);
      CtClass thunkType = method.getDeclaringClass();
      return new MethodCall(new ParamAccess(paramAccess.name, thunkType, paramAccess.index), method, Collections.<Expression>emptyList());
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
    else return paramAccess;
  }

  private final Map<Integer, CtMethod> lazyMap;
}
