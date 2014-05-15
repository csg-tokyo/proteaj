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
    addOperator(BinaryOperator.badd);
    addOperator(BinaryOperator.cadd);
    addOperator(BinaryOperator.sadd);
    addOperator(BinaryOperator.iadd);
    addOperator(BinaryOperator.ladd);
    addOperator(BinaryOperator.fadd);
    addOperator(BinaryOperator.dadd);
    addOperator(BinaryOperator.bsub);
    addOperator(BinaryOperator.csub);
    addOperator(BinaryOperator.ssub);
    addOperator(BinaryOperator.isub);
    addOperator(BinaryOperator.lsub);
    addOperator(BinaryOperator.fsub);
    addOperator(BinaryOperator.dsub);
    addOperator(BinaryOperator.bmul);
    addOperator(BinaryOperator.cmul);
    addOperator(BinaryOperator.smul);
    addOperator(BinaryOperator.imul);
    addOperator(BinaryOperator.lmul);
    addOperator(BinaryOperator.fmul);
    addOperator(BinaryOperator.dmul);
    addOperator(BinaryOperator.bdiv);
    addOperator(BinaryOperator.cdiv);
    addOperator(BinaryOperator.sdiv);
    addOperator(BinaryOperator.idiv);
    addOperator(BinaryOperator.ldiv);
    addOperator(BinaryOperator.fdiv);
    addOperator(BinaryOperator.ddiv);
    addOperator(BinaryOperator.brem);
    addOperator(BinaryOperator.crem);
    addOperator(BinaryOperator.srem);
    addOperator(BinaryOperator.irem);
    addOperator(BinaryOperator.lrem);
    addOperator(BinaryOperator.frem);
    addOperator(BinaryOperator.drem);
    addOperator(BinaryOperator.blt);
    addOperator(BinaryOperator.clt);
    addOperator(BinaryOperator.slt);
    addOperator(BinaryOperator.ilt);
    addOperator(BinaryOperator.llt);
    addOperator(BinaryOperator.flt);
    addOperator(BinaryOperator.dlt);
    addOperator(BinaryOperator.bgt);
    addOperator(BinaryOperator.cgt);
    addOperator(BinaryOperator.sgt);
    addOperator(BinaryOperator.igt);
    addOperator(BinaryOperator.lgt);
    addOperator(BinaryOperator.fgt);
    addOperator(BinaryOperator.dgt);
    addOperator(BinaryOperator.bleq);
    addOperator(BinaryOperator.cleq);
    addOperator(BinaryOperator.sleq);
    addOperator(BinaryOperator.ileq);
    addOperator(BinaryOperator.lleq);
    addOperator(BinaryOperator.fleq);
    addOperator(BinaryOperator.dleq);
    addOperator(BinaryOperator.bgeq);
    addOperator(BinaryOperator.cgeq);
    addOperator(BinaryOperator.sgeq);
    addOperator(BinaryOperator.igeq);
    addOperator(BinaryOperator.lgeq);
    addOperator(BinaryOperator.fgeq);
    addOperator(BinaryOperator.dgeq);
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
