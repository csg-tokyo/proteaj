package proteaj.ir.primitive;

import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class PrimitiveReadasOperator extends PrimitiveOperator {

  public PrimitiveReadasOperator(CtClass returnType) {
    super(returnType, IRDummyPattern.getDummy_Readas(), 0);
  }

  @Override
  public String toJavassistCode(List<Expression> operands) {
    assert false;
    throw new RuntimeException("this method is never called : PrimitiveReadasOperator.toJavassistCode");
  }
}
