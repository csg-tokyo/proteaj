package proteaj.codegen.javassist;

import proteaj.ir.*;
import proteaj.ir.primitive.*;
import proteaj.tast.*;
import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

import static proteaj.util.Escape.*;

public class ExpressionCodeGenerator extends ExpressionVisitor<StringBuilder> {
  public static final ExpressionCodeGenerator instance = new ExpressionCodeGenerator();

  @Override
  public StringBuilder visit(Operation operation, StringBuilder buf) {
    IROperator operator = operation.operator;
    List<Expression> operands = operation.operands;

    if (operator instanceof PrimitiveOperator) return codeGenPrimitiveOperator((PrimitiveOperator)operator, operands, buf);

    buf = buf.append(operator.getClassName()).append('.').append(operator.getMethodName());
    buf = buf.append('(');

    if (! operands.isEmpty()) buf = visit(operands.get(0), buf);
    for (int i = 1; i < operands.size(); i++) buf = visit(operands.get(i), buf.append(','));

    buf = buf.append(')');

    return buf;
  }

  @Override
  public StringBuilder visit(LocalVarDecl local, StringBuilder buf) {
    buf = buf.append(local.type.getName()).append(' ').append(local.name);
    if (local.val != null) {
      buf = buf.append(" = ");
      buf = visit(local.val, buf);
    }
    return buf;
  }

  @Override
  public StringBuilder visit(ExpressionList list, StringBuilder buf) {
    if (list.exprs.isEmpty()) return buf;

    buf = visit(list.exprs.get(0), buf);
    for (int i = 1; i < list.exprs.size(); i++) {
      buf = visit(list.exprs.get(i), buf.append(','));
    }
    return buf;
  }

  @Override
  public StringBuilder visit(AssignExpression assign, StringBuilder buf) {
    buf = visit(assign.ref, buf);
    buf = buf.append(" = ");
    buf = visit(assign.val, buf);
    return buf;
  }

  @Override
  public StringBuilder visit(MethodCall methodCall, StringBuilder buf) {
    buf = visit(methodCall.expr, buf);
    buf = buf.append('.').append(methodCall.method.getName()).append('(');

    List<Expression> args = methodCall.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')');
  }

  @Override
  public StringBuilder visit(StaticMethodCall methodCall, StringBuilder buf) {
    buf = buf.append(methodCall.method.getDeclaringClass().getName());
    buf = buf.append('.').append(methodCall.method.getName()).append('(');

    List<Expression> args = methodCall.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')');
  }

  @Override
  public StringBuilder visit(FieldAccess fieldAccess, StringBuilder buf) {
    buf = visit(fieldAccess.expr, buf);
    buf = buf.append('.').append(fieldAccess.field.getName());
    return buf;
  }

  @Override
  public StringBuilder visit(StaticFieldAccess fieldAccess, StringBuilder buf) {
    buf = buf.append(fieldAccess.field.getDeclaringClass().getName());
    buf = buf.append('.').append(fieldAccess.field.getName());
    return buf;
  }

  @Override
  public StringBuilder visit(NewExpression newExpression, StringBuilder buf) {
    buf = buf.append("new ").append(newExpression.constructor.getDeclaringClass().getName()).append('(');

    List<Expression> args = newExpression.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')');
  }

  @Override
  public StringBuilder visit(NewArrayExpression newArray, StringBuilder buf) {
    buf = buf.append("new ");

    int dim = 0;
    CtClass t = newArray.getType();
    while (t.isArray()) try {
      t = t.getComponentType();
      dim++;
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException("invalid array type");
    }
    buf = buf.append(t.getName());

    for (Expression arg : newArray.args) {
      buf = buf.append('[');
      buf = visit(arg, buf);
      buf = buf.append(']');
      dim--;
    }

    while (dim > 0) {
      buf.append("[]");
      dim--;
    }

    return buf;
  }

  @Override
  public StringBuilder visit(ArrayAccess arrayAccess, StringBuilder buf) {
    buf = visit(arrayAccess.array, buf);
    buf = buf.append('[');
    buf = visit(arrayAccess.index, buf);
    buf = buf.append(']');
    return buf;
  }

  @Override
  public StringBuilder visit(ArrayLength arrayLength, StringBuilder buf) {
    return visit(arrayLength.array, buf).append(".length");
  }

  @Override
  public StringBuilder visit(ThisExpression thisExpr, StringBuilder buf) {
    return buf.append("this");
  }

  @Override
  public StringBuilder visit(SuperExpression superExpr, StringBuilder buf) {
    return buf.append("super");
  }

  @Override
  public StringBuilder visit(ParamAccess paramAccess, StringBuilder buf) {
    return buf.append("$").append(paramAccess.index + 1);
  }

  @Override
  public StringBuilder visit(LocalVariable local, StringBuilder buf) {
    return buf.append(local.name);
  }

  @Override
  public StringBuilder visit(CastExpression castExpr, StringBuilder buf) {
    CtClass type = castExpr.getType();
    if (type == CtClass.voidType) return visit(castExpr.expr, buf);

    buf = buf.append('(').append('(').append(type.getName()).append(')');
    buf = visit(castExpr.expr, buf);
    buf = buf.append(')');
    return buf;
  }

  @Override
  public StringBuilder visit(VariableArguments operands, StringBuilder buf) {
    buf = buf.append("new ").append(operands.getType().getName());
    buf = buf.append('{');

    List<Expression> args = operands.args;
    if (! args.isEmpty()) buf = visit(args.get(0), buf);

    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    buf = buf.append('}');
    return buf;
  }

  @Override
  public StringBuilder visit(StringLiteral string, StringBuilder buf) {
    return buf.append('"').append(escape(string.str)).append('"');
  }

  @Override
  public StringBuilder visit(CharLiteral ch, StringBuilder buf) {
    return buf.append('\'').append(escape(ch.val)).append('\'');
  }

  @Override
  public StringBuilder visit(IntLiteral i, StringBuilder buf) {
    return buf.append(i.val);
  }

  @Override
  public StringBuilder visit(BooleanLiteral bool, StringBuilder buf) {
    return buf.append(bool.val);
  }

  @Override
  public StringBuilder visit(ClassLiteral clazz, StringBuilder buf) {
    return buf.append(clazz.cls.getName()).append(".class");
  }

  @Override
  public StringBuilder visit(NullLiteral nul, StringBuilder buf) {
    return buf.append("null");
  }

  private StringBuilder codeGenPrimitiveOperator (PrimitiveOperator operator, List<Expression> operands, StringBuilder buf) {
    buf = buf.append('(');
    if (operator instanceof PrefixOperator) {
      assert operands.size() == 1;
      String name = ((PrefixOperator)operator).operator;
      buf = buf.append(name);
      buf = visit(operands.get(0), buf);
    }
    else if (operator instanceof PostfixOperator) {
      assert operands.size() == 1;
      String name = ((PostfixOperator)operator).operator;
      buf = visit(operands.get(0), buf);
      buf = buf.append(name);
    }
    else if (operator instanceof BinaryOperator) {
      assert operands.size() == 2;
      String name = ((BinaryOperator)operator).operator;
      buf = visit(operands.get(0), buf);
      buf = buf.append(' ').append(name).append(' ');
      buf = visit(operands.get(1), buf);
    }
    else {
      assert false;
      throw new RuntimeException("unknown primitive operator");
    }
    buf = buf.append(')');
    return buf;
  }

  private ExpressionCodeGenerator() {}
}
