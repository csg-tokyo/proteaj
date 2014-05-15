package proteaj.ir.primitive;

import proteaj.ir.*;

public class PrimitiveSyntax extends IRSyntax {

  public static PrimitiveSyntax getSyntax() {
    if(syntax == null) syntax = new PrimitiveSyntax();
    return syntax;
  }

  private static PrimitiveSyntax syntax = null;

  private PrimitiveSyntax() {
    super("proteaj.lang.PrimitiveOperators");
    addOperator(BinaryOperator.plus);
    addOperator(BinaryOperator.minus);
    addOperator(BinaryOperator.mul);
    addOperator(BinaryOperator.div);
    addOperator(BinaryOperator.mod);
    addOperator(BinaryOperator.lthan);
    addOperator(BinaryOperator.gthan);
    addOperator(BinaryOperator.leq);
    addOperator(BinaryOperator.geq);
    addOperator(BinaryOperator.bequal);
    addOperator(BinaryOperator.cequal);
    addOperator(BinaryOperator.sequal);
    addOperator(BinaryOperator.iequal);
    addOperator(BinaryOperator.lequal);
    addOperator(BinaryOperator.bneq);
    addOperator(BinaryOperator.cneq);
    addOperator(BinaryOperator.sneq);
    addOperator(BinaryOperator.ineq);
    addOperator(BinaryOperator.lneq);
    addOperator(BinaryOperator.bitand);
    addOperator(BinaryOperator.bitxor);
    addOperator(BinaryOperator.bitor);
    addOperator(BinaryOperator.getObjEqOperator());
    addOperator(BinaryOperator.getObjNeqOperator());
    addOperator(BinaryOperator.and);
    addOperator(BinaryOperator.or);
    addOperator(PrefixOperator.plus);
    addOperator(PrefixOperator.minus);
    addOperator(PrefixOperator.inc);
    addOperator(PrefixOperator.dec);
    addOperator(PrefixOperator.incv);
    addOperator(PrefixOperator.decv);
    addOperator(PrefixOperator.not);
    addOperator(PrefixOperator.compl);
    addOperator(PostfixOperator.inc);
    addOperator(PostfixOperator.dec);
    addOperator(PostfixOperator.incv);
    addOperator(PostfixOperator.decv);
  }
}
