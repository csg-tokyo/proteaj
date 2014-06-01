package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.util.*;

import javassist.*;

public class PrefixOperator extends PrimitiveOperator {

  public static final PrefixOperator bplus = new PrefixOperator(CtClass.byteType, "+", 1400);
  public static final PrefixOperator splus = new PrefixOperator(CtClass.shortType, "+", 1400);
  public static final PrefixOperator iplus = new PrefixOperator(CtClass.intType, "+", 1400);
  public static final PrefixOperator lplus = new PrefixOperator(CtClass.longType, "+", 1400);
  public static final PrefixOperator fplus = new PrefixOperator(CtClass.floatType, "+", 1400);
  public static final PrefixOperator dplus = new PrefixOperator(CtClass.doubleType, "+", 1400);

  public static final PrefixOperator bneg = new PrefixOperator(CtClass.byteType, "-", 1400);
  public static final PrefixOperator sneg = new PrefixOperator(CtClass.shortType, "-", 1400);
  public static final PrefixOperator ineg = new PrefixOperator(CtClass.intType, "-", 1400);
  public static final PrefixOperator lneg = new PrefixOperator(CtClass.longType, "-", 1400);
  public static final PrefixOperator fneg = new PrefixOperator(CtClass.floatType, "-", 1400);
  public static final PrefixOperator dneg = new PrefixOperator(CtClass.doubleType, "-", 1400);

  public static final PrefixOperator binc_pre = new PrefixOperator(CtClass.byteType, "++", 1400);
  public static final PrefixOperator sinc_pre = new PrefixOperator(CtClass.shortType, "++", 1400);
  public static final PrefixOperator iinc_pre = new PrefixOperator(CtClass.intType, "++", 1400);
  public static final PrefixOperator linc_pre = new PrefixOperator(CtClass.longType, "++", 1400);
  public static final PrefixOperator finc_pre = new PrefixOperator(CtClass.floatType, "++", 1400);
  public static final PrefixOperator dinc_pre = new PrefixOperator(CtClass.doubleType, "++", 1400);

  public static final PrefixOperator bdec_pre = new PrefixOperator(CtClass.byteType, "--", 1400);
  public static final PrefixOperator sdec_pre = new PrefixOperator(CtClass.shortType, "--", 1400);
  public static final PrefixOperator idec_pre = new PrefixOperator(CtClass.intType, "--", 1400);
  public static final PrefixOperator ldec_pre = new PrefixOperator(CtClass.longType, "--", 1400);
  public static final PrefixOperator fdec_pre = new PrefixOperator(CtClass.floatType, "--", 1400);
  public static final PrefixOperator ddec_pre = new PrefixOperator(CtClass.doubleType, "--", 1400);

  public static final PrefixOperator incv_pre  = new PrefixOperator(CtClass.voidType, "++", CtClass.intType, 1400);
  public static final PrefixOperator decv_pre  = new PrefixOperator(CtClass.voidType, "--", CtClass.intType, 1400);
  
  public static final PrefixOperator not   = new PrefixOperator(CtClass.booleanType, "!", 1400);
  public static final PrefixOperator compl = new PrefixOperator(CtClass.intType, "~", 1400);

  private static IRPattern getPrefixOperatorPattern(String operator, CtClass type) {
    OperatorPattern pattern = new OperatorPattern(-1);

    pattern.append(new Operator(operator, -1));
    pattern.append(new Operand("operand", -1));

    CtClass[] paramTypes = { type };
    IROperandAttribute[] paramMods = { new IROperandAttribute(0) };

    return new IRPattern(MOD_NONASSOC, pattern, paramTypes, paramMods, getEmptyCtClassArray(), getEmptyCtClassArray());
  }

  private PrefixOperator(CtClass returnType, String operator, CtClass type, int priority) {
    super(returnType, getPrefixOperatorPattern(operator, type), priority);
    this.operator = operator;
  }

  private PrefixOperator(CtClass type, String operator, int priority) {
    super(type, getPrefixOperatorPattern(operator, type), priority);
    this.operator = operator;
  }

  public final String operator;
  private static final int MOD_NONASSOC = Modifiers.PUBLIC | Modifiers.STATIC | Modifiers.NON_ASSOC;
}

