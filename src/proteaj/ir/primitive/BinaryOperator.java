package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.env.type.*;
import proteaj.util.*;

import javassist.*;

public class BinaryOperator extends PrimitiveOperator {

  public static final BinaryOperator badd = new BinaryOperator(CtClass.byteType, "+", 900);
  public static final BinaryOperator cadd = new BinaryOperator(CtClass.charType, "+", 900);
  public static final BinaryOperator sadd = new BinaryOperator(CtClass.shortType, "+", 900);
  public static final BinaryOperator iadd = new BinaryOperator(CtClass.intType, "+", 900);
  public static final BinaryOperator ladd = new BinaryOperator(CtClass.longType, "+", 900);
  public static final BinaryOperator fadd = new BinaryOperator(CtClass.floatType, "+", 900);
  public static final BinaryOperator dadd = new BinaryOperator(CtClass.doubleType, "+", 900);

  public static final BinaryOperator bsub = new BinaryOperator(CtClass.byteType, "-", 900);
  public static final BinaryOperator csub = new BinaryOperator(CtClass.charType, "-", 900);
  public static final BinaryOperator ssub = new BinaryOperator(CtClass.shortType, "-", 900);
  public static final BinaryOperator isub = new BinaryOperator(CtClass.intType, "-", 900);
  public static final BinaryOperator lsub = new BinaryOperator(CtClass.longType, "-", 900);
  public static final BinaryOperator fsub = new BinaryOperator(CtClass.floatType, "-", 900);
  public static final BinaryOperator dsub = new BinaryOperator(CtClass.doubleType, "-", 900);

  public static final BinaryOperator bmul = new BinaryOperator(CtClass.byteType, "*", 1000);
  public static final BinaryOperator cmul = new BinaryOperator(CtClass.charType, "*", 1000);
  public static final BinaryOperator smul = new BinaryOperator(CtClass.shortType, "*", 1000);
  public static final BinaryOperator imul = new BinaryOperator(CtClass.intType, "*", 1000);
  public static final BinaryOperator lmul = new BinaryOperator(CtClass.longType, "*", 1000);
  public static final BinaryOperator fmul = new BinaryOperator(CtClass.floatType, "*", 1000);
  public static final BinaryOperator dmul = new BinaryOperator(CtClass.doubleType, "*", 1000);

  public static final BinaryOperator bdiv = new BinaryOperator(CtClass.byteType, "/", 1000);
  public static final BinaryOperator cdiv = new BinaryOperator(CtClass.charType, "/", 1000);
  public static final BinaryOperator sdiv = new BinaryOperator(CtClass.shortType, "/", 1000);
  public static final BinaryOperator idiv = new BinaryOperator(CtClass.intType, "/", 1000);
  public static final BinaryOperator ldiv = new BinaryOperator(CtClass.longType, "/", 1000);
  public static final BinaryOperator fdiv = new BinaryOperator(CtClass.floatType, "/", 1000);
  public static final BinaryOperator ddiv = new BinaryOperator(CtClass.doubleType, "/", 1000);

  public static final BinaryOperator brem = new BinaryOperator(CtClass.byteType, "%", 1000);
  public static final BinaryOperator crem = new BinaryOperator(CtClass.charType, "%", 1000);
  public static final BinaryOperator srem = new BinaryOperator(CtClass.shortType, "%", 1000);
  public static final BinaryOperator irem = new BinaryOperator(CtClass.intType, "%", 1000);
  public static final BinaryOperator lrem = new BinaryOperator(CtClass.longType, "%", 1000);
  public static final BinaryOperator frem = new BinaryOperator(CtClass.floatType, "%", 1000);
  public static final BinaryOperator drem = new BinaryOperator(CtClass.doubleType, "%", 1000);

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

  private static BinaryOperator instanceOf = null;
  private static BinaryOperator objEquals  = null;
  private static BinaryOperator objNotEqs  = null;

  private static void initialize () {
    if (instanceOf == null && objEquals == null && objNotEqs == null) {
      CommonTypes cts = CommonTypes.getInstance();
      instanceOf = new BinaryOperator(CtClass.booleanType, cts.objectType, "instanceof", cts.typeType, 700);
      objEquals  = new BinaryOperator(CtClass.booleanType, cts.objectType, "==", cts.objectType, 600);
      objNotEqs  = new BinaryOperator(CtClass.booleanType, cts.objectType, "!=", cts.objectType, 600);
    }
  }

  public static BinaryOperator getInstanceOfOperator() {
    initialize();
    return instanceOf;
  }

  public static BinaryOperator getObjEqOperator() {
    initialize();
    return objEquals;
  }

  public static BinaryOperator getObjNeqOperator() {
    initialize();
    return objNotEqs;
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

