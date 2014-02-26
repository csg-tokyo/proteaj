package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class PrefixOperator extends PrimitiveOperator {

  public static final PrefixOperator plus  = new PrefixOperator(CtClass.intType, "+", 1400);
  public static final PrefixOperator minus = new PrefixOperator(CtClass.intType, "-", 1400);
  public static final PrefixOperator inc   = new PrefixOperator(CtClass.intType, "++", 1400);
  public static final PrefixOperator dec   = new PrefixOperator(CtClass.intType, "--", 1400);
  public static final PrefixOperator incv  = new PrefixOperator(CtClass.voidType, "++", CtClass.intType, 1400);
  public static final PrefixOperator decv  = new PrefixOperator(CtClass.voidType, "--", CtClass.intType, 1400);
  public static final PrefixOperator not   = new PrefixOperator(CtClass.booleanType, "!", 1400);

  @Override
  public String toJavassistCode(List<Expression> operands) {
    assert operands.size() == 1;
    return '(' + operator + operands.get(0).toJavassistCode() + ')';
  }

  private static IRPattern getPrefixOperatorPattern(String operator, CtClass type) {
    OperatorPattern pattern = new OperatorPattern(-1);

    pattern.append(new Operator(operator, -1));
    pattern.append(new Operand("operand", -1));

    CtClass[] paramTypes = { type };
    IROperandAttribute[] paramMods = { new IROperandAttribute(0) };

    return new IRPattern(MOD_RASSOC, pattern, paramTypes, paramMods, getEmptyCtClassArray(), getEmptyCtClassArray());
  }

  private PrefixOperator(CtClass returnType, String operator, CtClass type, int priority) {
    super(returnType, getPrefixOperatorPattern(operator, type), priority);
    this.operator = operator;
  }

  private PrefixOperator(CtClass type, String operator, int priority) {
    super(type, getPrefixOperatorPattern(operator, type), priority);
    this.operator = operator;
  }

  private String operator;
  private static final int MOD_RASSOC = Modifiers.PUBLIC | Modifiers.STATIC | Modifiers.RIGHT_ASSOC;
}

