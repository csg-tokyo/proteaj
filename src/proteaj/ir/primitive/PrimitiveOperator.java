package proteaj.ir.primitive;

import proteaj.ir.*;

import javassist.*;

import java.util.Arrays;
import java.util.Collections;

public abstract class PrimitiveOperator extends IROperator {
  protected PrimitiveOperator(CtClass returnType, IRPattern pattern, int priority) {
    super(returnType, Arrays.asList(returnType), pattern, priority);
  }

  protected static CtClass[] getEmptyCtClassArray() {
    if(emptyCtClassArray != null) return emptyCtClassArray;
    emptyCtClassArray = new CtClass[0];
    return emptyCtClassArray;
  }

  private static CtClass[] emptyCtClassArray = null;
}

