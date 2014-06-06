package proteaj.ir.primitive;

import proteaj.ir.*;

import static proteaj.ir.primitive.BinaryOperator.*;
import static proteaj.ir.primitive.PrefixOperator.*;
import static proteaj.ir.primitive.PostfixOperator.*;

public class PrimitiveSyntax extends IRSyntax {

  public static PrimitiveSyntax getSyntax() {
    if(syntax == null) syntax = new PrimitiveSyntax();
    return syntax;
  }

  private static PrimitiveSyntax syntax = null;

  private PrimitiveSyntax() {
    super("proteaj.lang.PrimitiveOperators", "(no source)");

    addOperators(
        badd, cadd, sadd, iadd, ladd, fadd, dadd,
        bsub, csub, ssub, isub, lsub, fsub, dsub,
        bmul, cmul, smul, imul, lmul, fmul, dmul,
        bdiv, cdiv, sdiv, idiv, ldiv, fdiv, ddiv,
        brem, crem, srem, irem, lrem, frem, drem,
        lsh, rsh, lrsh,
        blt, clt, slt, ilt, llt, flt, dlt,
        bgt, cgt, sgt, igt, lgt, fgt, dgt,
        bleq, cleq, sleq, ileq, lleq, fleq, dleq,
        bgeq, cgeq, sgeq, igeq, lgeq, fgeq, dgeq,
        bequal, cequal, sequal, iequal, lequal,
        bneq, cneq, sneq, ineq, lneq,
        bitand, bitxor, bitor, and, or,
        bplus, splus, iplus, lplus, fplus, dplus,
        bneg, sneg, ineg, lneg, fneg, dneg,
        binc_pre, sinc_pre, iinc_pre, linc_pre, finc_pre, dinc_pre,
        bdec_pre, sdec_pre, idec_pre, ldec_pre, fdec_pre, ddec_pre,
        incv_pre, decv_pre,
        not, compl,
        binc_post, sinc_post, iinc_post, linc_post, finc_post, dinc_post,
        bdec_post, sdec_post, idec_post, ldec_post, fdec_post, ddec_post,
        incv_post, decv_post
    );

    addOperator(getInstanceOfOperator());
    addOperator(getObjEqOperator());
    addOperator(getObjNeqOperator());
  }
}
