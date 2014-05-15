package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.util.*;

import javassist.*;

public class BinaryOperator extends PrimitiveOperator {

  public static final BinaryOperator plus   = new BinaryOperator(CtClass.intType, "+", 900);
  public static final BinaryOperator minus  = new BinaryOperator(CtClass.intType, "-", 900);
  public static final BinaryOperator mul    = new BinaryOperator(CtClass.intType, "*", 1000);
  public static final BinaryOperator div    = new BinaryOperator(CtClass.intType, "/", 1000);
  public static final BinaryOperator mod    = new BinaryOperator(CtClass.intType, "%", 1000);

  public static final BinaryOperator blt  = new BinaryOperator(CtClass.booleanType, CtClass.byteType, "<", CtClass.byteType, 700);
  public static final BinaryOperator clt  = new BinaryOperator(CtClass.booleanType, CtClass.charType, "<", CtClass.charType, 700);
  public static final BinaryOperator slt  = new BinaryOperator(CtClass.booleanType, CtClass.shortType, "<", CtClass.shortType, 700);
  public static final BinaryOperator ilt  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "<", CtClass.intType, 700);
  public static final BinaryOperator llt  = new BinaryOperator(CtClass.booleanType, CtClass.longType, "<", CtClass.longType, 700);
  public static final BinaryOperator flt  = new BinaryOperator(CtClass.booleanType, CtClass.floatType, "<", CtClass.floatType, 700);
  public static final BinaryOperator dlt  = new BinaryOperator(CtClass.booleanType, CtClass.doubleType, "<", CtClass.doubleType, 700);

  public static final BinaryOperator bgt  = new BinaryOperator(CtClass.booleanType, CtClass.byteType, ">", CtClass.byteType, 700);
  public static final BinaryOperator cgt  = new BinaryOperator(CtClass.booleanType, CtClass.charType, ">", CtClass.charType, 700);
  public static final BinaryOperator sgt  = new BinaryOperator(CtClass.booleanType, CtClass.shortType, ">", CtClass.shortType, 700);
  public static final BinaryOperator igt  = new BinaryOperator(CtClass.booleanType, CtClass.intType, ">", CtClass.intType, 700);
  public static final BinaryOperator lgt  = new BinaryOperator(CtClass.booleanType, CtClass.longType, ">", CtClass.longType, 700);
  public static final BinaryOperator fgt  = new BinaryOperator(CtClass.booleanType, CtClass.floatType, ">", CtClass.floatType, 700);
  public static final BinaryOperator dgt  = new BinaryOperator(CtClass.booleanType, CtClass.doubleType, ">", CtClass.doubleType, 700);

  public static final BinaryOperator bleq  = new BinaryOperator(CtClass.booleanType, CtClass.byteType, "<=", CtClass.byteType, 700);
  public static final BinaryOperator cleq  = new BinaryOperator(CtClass.booleanType, CtClass.charType, "<=", CtClass.charType, 700);
  public static final BinaryOperator sleq  = new BinaryOperator(CtClass.booleanType, CtClass.shortType, "<=", CtClass.shortType, 700);
  public static final BinaryOperator ileq  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "<=", CtClass.intType, 700);
  public static final BinaryOperator lleq  = new BinaryOperator(CtClass.booleanType, CtClass.longType, "<=", CtClass.longType, 700);
  public static final BinaryOperator fleq  = new BinaryOperator(CtClass.booleanType, CtClass.floatType, "<=", CtClass.floatType, 700);
  public static final BinaryOperator dleq  = new BinaryOperator(CtClass.booleanType, CtClass.doubleType, "<=", CtClass.doubleType, 700);

  public static final BinaryOperator bgeq  = new BinaryOperator(CtClass.booleanType, CtClass.byteType, ">=", CtClass.byteType, 700);
  public static final BinaryOperator cgeq  = new BinaryOperator(CtClass.booleanType, CtClass.charType, ">=", CtClass.charType, 700);
  public static final BinaryOperator sgeq  = new BinaryOperator(CtClass.booleanType, CtClass.shortType, ">=", CtClass.shortType, 700);
  public static final BinaryOperator igeq  = new BinaryOperator(CtClass.booleanType, CtClass.intType, ">=", CtClass.intType, 700);
  public static final BinaryOperator lgeq  = new BinaryOperator(CtClass.booleanType, CtClass.longType, ">=", CtClass.longType, 700);
  public static final BinaryOperator fgeq  = new BinaryOperator(CtClass.booleanType, CtClass.floatType, ">=", CtClass.floatType, 700);
  public static final BinaryOperator dgeq  = new BinaryOperator(CtClass.booleanType, CtClass.doubleType, ">=", CtClass.doubleType, 700);

  public static final BinaryOperator bequal = new BinaryOperator(CtClass.booleanType, CtClass.byteType, "==", CtClass.byteType, 600);
  public static final BinaryOperator cequal = new BinaryOperator(CtClass.booleanType, CtClass.charType, "==", CtClass.charType, 600);
  public static final BinaryOperator sequal = new BinaryOperator(CtClass.booleanType, CtClass.shortType, "==", CtClass.shortType, 600);
  public static final BinaryOperator iequal = new BinaryOperator(CtClass.booleanType, CtClass.intType, "==", CtClass.intType, 600);
  public static final BinaryOperator lequal = new BinaryOperator(CtClass.booleanType, CtClass.longType, "==", CtClass.longType, 600);

  public static final BinaryOperator bneq  = new BinaryOperator(CtClass.booleanType, CtClass.byteType, "!=", CtClass.byteType, 600);
  public static final BinaryOperator cneq  = new BinaryOperator(CtClass.booleanType, CtClass.charType, "!=", CtClass.charType, 600);
  public static final BinaryOperator sneq  = new BinaryOperator(CtClass.booleanType, CtClass.shortType, "!=", CtClass.shortType, 600);
  public static final BinaryOperator ineq  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "!=", CtClass.intType, 600);
  public static final BinaryOperator lneq  = new BinaryOperator(CtClass.booleanType, CtClass.longType, "!=", CtClass.longType, 600);

  public static final BinaryOperator bitand = new BinaryOperator(CtClass.intType, "&", 500);
  public static final BinaryOperator bitxor = new BinaryOperator(CtClass.intType, "^", 400);
  public static final BinaryOperator bitor  = new BinaryOperator(CtClass.intType, "|", 300);
  public static final BinaryOperator and    = new BinaryOperator(CtClass.booleanType, CtClass.booleanType, "&&", CtClass.booleanType, 200);
  public static final BinaryOperator or     = new BinaryOperator(CtClass.booleanType, CtClass.booleanType, "||", CtClass.booleanType, 100);

  public static BinaryOperator getObjEqOperator() {
    return new BinaryOperator(CtClass.booleanType, IRCommonTypes.getObjectType(), "==", IRCommonTypes.getObjectType(), 600);
  }

  public static BinaryOperator getObjNeqOperator() {
    return new BinaryOperator(CtClass.booleanType, IRCommonTypes.getObjectType(), "!=", IRCommonTypes.getObjectType(), 600);
  }

  private static IRPattern getBinaryOperatorPattern(CtClass left, String operator, CtClass right) {
    OperatorPattern pattern = new OperatorPattern(-1);

    pattern.append(new Operand("left", -1));
    pattern.append(new Operator(operator, -1));
    pattern.append(new Operand("right", -1));

    CtClass[] paramTypes = { left, right };
    IROperandAttribute[] paramMods = { new IROperandAttribute(0), new IROperandAttribute(0) };

    return new IRPattern(MOD_LASSOC, pattern, paramTypes, paramMods, getEmptyCtClassArray(), getEmptyCtClassArray());
  }

  private BinaryOperator(CtClass returnType, CtClass left, String operator, CtClass right, int priority) {
    super(returnType, getBinaryOperatorPattern(left, operator, right), priority);
    this.operator = operator;
  }

  private BinaryOperator(CtClass type, String operator, int priority) {
    super(type, getBinaryOperatorPattern(type, operator, type), priority);
    this.operator = operator;
  }

  public final String operator;
  private static final int MOD_LASSOC = Modifiers.PUBLIC | Modifiers.STATIC;
}

