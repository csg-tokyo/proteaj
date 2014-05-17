package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.util.*;

import javassist.*;

public class PostfixOperator extends PrimitiveOperator {

  public static final PostfixOperator binc_post = new PostfixOperator(CtClass.byteType, "++", 1500);
  public static final PostfixOperator sinc_post = new PostfixOperator(CtClass.shortType, "++", 1500);
  public static final PostfixOperator iinc_post = new PostfixOperator(CtClass.intType, "++", 1500);
  public static final PostfixOperator linc_post = new PostfixOperator(CtClass.longType, "++", 1500);
  public static final PostfixOperator finc_post = new PostfixOperator(CtClass.floatType, "++", 1500);
  public static final PostfixOperator dinc_post = new PostfixOperator(CtClass.doubleType, "++", 1500);

  public static final PostfixOperator bdec_post = new PostfixOperator(CtClass.byteType, "--", 1500);
  public static final PostfixOperator sdec_post = new PostfixOperator(CtClass.shortType, "--", 1500);
  public static final PostfixOperator idec_post = new PostfixOperator(CtClass.intType, "--", 1500);
  public static final PostfixOperator ldec_post = new PostfixOperator(CtClass.longType, "--", 1500);
  public static final PostfixOperator fdec_post = new PostfixOperator(CtClass.floatType, "--", 1500);
  public static final PostfixOperator ddec_post = new PostfixOperator(CtClass.doubleType, "--", 1500);

  public static final PostfixOperator incv_post = new PostfixOperator(CtClass.voidType, CtClass.intType, "++", 1500);
  public static final PostfixOperator decv_post = new PostfixOperator(CtClass.voidType, CtClass.intType, "--", 1500);

  private static IRPattern getPostfixOperatorPattern(CtClass type, String operator) {
    OperatorPattern pattern = new OperatorPattern(-1);

    pattern.append(new Operand("operand", -1));
    pattern.append(new Operator(operator, -1));

    CtClass[] paramTypes = { type };
    IROperandAttribute[] paramMods = { new IROperandAttribute(0) };

    return new IRPattern(MOD_NASSOC, pattern, paramTypes, paramMods, getEmptyCtClassArray(), getEmptyCtClassArray());
  }

  private PostfixOperator(CtClass returnType, CtClass type, String operator, int priority) {
    super(returnType, getPostfixOperatorPattern(type, operator), priority);
    this.operator = operator;
  }

  private PostfixOperator(CtClass type, String operator, int priority) {
    super(type, getPostfixOperatorPattern(type, operator), priority);
    this.operator = operator;
  }

  public final String operator;
  private static final int MOD_NASSOC = Modifiers.PUBLIC | Modifiers.STATIC | Modifiers.NON_ASSOC;
}

