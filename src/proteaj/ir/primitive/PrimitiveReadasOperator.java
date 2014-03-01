package proteaj.ir.primitive;

import proteaj.ir.*;

import javassist.*;

public class PrimitiveReadasOperator extends PrimitiveOperator {

  public PrimitiveReadasOperator(CtClass returnType) {
    super(returnType, IRDummyPattern.getDummy_Readas(), 0);
  }
}
