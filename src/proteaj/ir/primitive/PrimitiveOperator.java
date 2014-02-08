package proteaj.ir.primitive;

import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public abstract class PrimitiveOperator extends IROperator {
  protected PrimitiveOperator(CtClass returnType, IRPattern pattern, int priority) {
    super(returnType, pattern, priority);
  }

  protected static CtClass[] getEmptyCtClassArray() {
    if(emptyCtClassArray != null) return emptyCtClassArray;
    emptyCtClassArray = new CtClass[0];
    return emptyCtClassArray;
  }

  private static CtClass[] emptyCtClassArray = null;

  @Override
  public abstract String toJavassistCode(List<Expression> operands);
}

