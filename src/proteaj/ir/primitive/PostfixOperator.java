package proteaj.ir.primitive;

import proteaj.ast.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class PostfixOperator extends PrimitiveOperator {

  public static final PostfixOperator inc  = new PostfixOperator(CtClass.intType, "++", 1500);
  public static final PostfixOperator dec  = new PostfixOperator(CtClass.intType, "--", 1500);
  public static final PostfixOperator incv = new PostfixOperator(CtClass.voidType, CtClass.intType, "++", 1500);
  public static final PostfixOperator decv = new PostfixOperator(CtClass.voidType, CtClass.intType, "--", 1500);

  @Override
  public String toJavassistCode(List<Expression> operands) {
    assert operands.size() == 1;
    return '(' + operands.get(0).toJavassistCode() + operator + ')';
  }

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

  private String operator;
  private static final int MOD_NASSOC = Modifiers.PUBLIC | Modifiers.STATIC | Modifiers.NON_ASSOC;
}

