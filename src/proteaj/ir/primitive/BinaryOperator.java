package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class BinaryOperator extends PrimitiveOperator {

  public static final BinaryOperator plus   = new BinaryOperator(CtClass.intType, "+", 900);
  public static final BinaryOperator minus  = new BinaryOperator(CtClass.intType, "-", 900);
  public static final BinaryOperator mul    = new BinaryOperator(CtClass.intType, "*", 1000);
  public static final BinaryOperator div    = new BinaryOperator(CtClass.intType, "/", 1000);
  public static final BinaryOperator mod    = new BinaryOperator(CtClass.intType, "%", 1000);
  public static final BinaryOperator lthan  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "<", CtClass.intType, 700);
  public static final BinaryOperator gthan  = new BinaryOperator(CtClass.booleanType, CtClass.intType, ">", CtClass.intType, 700);
  public static final BinaryOperator leq    = new BinaryOperator(CtClass.booleanType, CtClass.intType, "<=", CtClass.intType, 700);
  public static final BinaryOperator geq    = new BinaryOperator(CtClass.booleanType, CtClass.intType, ">=", CtClass.intType, 700);
  public static final BinaryOperator equal  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "==", CtClass.intType, 600);
  public static final BinaryOperator noteq  = new BinaryOperator(CtClass.booleanType, CtClass.intType, "!=", CtClass.intType, 600);
  public static final BinaryOperator and    = new BinaryOperator(CtClass.booleanType, CtClass.booleanType, "&&", CtClass.booleanType, 200);
  public static final BinaryOperator or     = new BinaryOperator(CtClass.booleanType, CtClass.booleanType, "||", CtClass.booleanType, 100);

  public static BinaryOperator getObjEqOperator() {
    return new BinaryOperator(CtClass.booleanType, IRCommonTypes.getObjectType(), "==", IRCommonTypes.getObjectType(), 600);
  }

  public static BinaryOperator getObjNeqOperator() {
    return new BinaryOperator(CtClass.booleanType, IRCommonTypes.getObjectType(), "!=", IRCommonTypes.getObjectType(), 600);
  }

  @Override
  public String toJavassistCode(List<Expression> operands) {
    assert operands.size() == 2;
    return '(' + operands.get(0).toJavassistCode() + " " + operator + " " + operands.get(1).toJavassistCode() + ')';
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

  private String operator;
  private static final int MOD_LASSOC = Modifiers.PUBLIC | Modifiers.STATIC;
}

