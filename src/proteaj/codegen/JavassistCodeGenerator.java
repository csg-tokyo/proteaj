package proteaj.codegen;

import proteaj.error.ForDebug;
import proteaj.ir.*;
import proteaj.ir.primitive.*;
import proteaj.tast.*;
import proteaj.tast.util.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

import static proteaj.util.Escape.escape;

public class JavassistCodeGenerator implements ExpressionVisitor<StringBuilder>, StatementVisitor<StringBuilder> {
  public static String codeGen (MethodBody body) {
    String code = instance.visit(body, new StringBuilder()).toString();
    ForDebug.print(code);
    return code;
  }

  public static String codeGen (ConstructorBody body) {
    String code = instance.visit(body, new StringBuilder()).toString();
    ForDebug.print(code);
    return code;
  }

  public static String codeGen (FieldBody body) {
    String code = instance.visit(body, new StringBuilder()).toString();
    ForDebug.print(code);
    return code;
  }

  public static String codeGen (DefaultValue body) {
    String code = instance.visit(body, new StringBuilder()).toString();
    ForDebug.print(code);
    return code;
  }

  public static String codeGen (ClassInitializer body) {
    String code = instance.visit(body, new StringBuilder()).toString();
    ForDebug.print(code);
    return code;
  }

  public static final JavassistCodeGenerator instance = new JavassistCodeGenerator();

  public StringBuilder visit (MethodBody body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  public StringBuilder visit (ConstructorBody body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  public StringBuilder visit (FieldBody body, StringBuilder buf) {
    return visit(body.expr, buf);
  }

  public StringBuilder visit (DefaultValue body, StringBuilder buf) {
    buf = buf.append('{').append('\n').append("return ");
    buf = visit(body.expr, buf);
    buf = buf.append(';').append('\n').append('}');
    return buf;
  }

  public StringBuilder visit (ClassInitializer body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  public StringBuilder visit(Statement stmt, StringBuilder buf) {
    return stmt.accept(this, buf);
  }

  @Override
  public StringBuilder visit(Block block, StringBuilder buf) {
    buf = buf.append('{');
    for (Statement s : block.getStatements()) buf = visit(s, buf.append('\n'));
    buf = buf.append('\n').append('}');
    return buf;
  }

  @Override
  public StringBuilder visit(ThisConstructorCall thisStmt, StringBuilder buf) {
    buf = buf.append("this").append('(');

    List<Expression> args = thisStmt.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')').append(';');
  }

  @Override
  public StringBuilder visit(SuperConstructorCall superStmt, StringBuilder buf) {
    buf = buf.append("super").append('(');

    List<Expression> args = superStmt.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')').append(';');
  }

  @Override
  public StringBuilder visit(LocalVarDeclStatement localDecl, StringBuilder buf) {
    buf = visit(localDecl.lvdecl, buf);
    return buf.append(';');
  }

  @Override
  public StringBuilder visit(IfStatement ifStmt, StringBuilder buf) {
    buf = buf.append("if ").append('(');
    buf = visit(ifStmt.condition, buf);
    buf = buf.append(')');
    buf = visit(ifStmt.thenStmt, buf);
    if (ifStmt.elseStmt != null) {
      buf = buf.append("else ");
      buf = visit(ifStmt.elseStmt, buf);
    }
    return buf;
  }

  @Override
  public StringBuilder visit(WhileStatement whileStmt, StringBuilder buf) {
    buf = buf.append("while ").append('(');
    buf = visit(whileStmt.condition, buf);
    buf = buf.append(')');
    buf = visit(whileStmt.stmt, buf);
    return buf;
  }

  @Override
  public StringBuilder visit(DoWhileStatement doWhileStmt, StringBuilder buf) {
    buf = buf.append("do ");
    buf = visit(doWhileStmt.stmt, buf);
    buf = buf.append("while ").append('(');
    buf = visit(doWhileStmt.condition, buf);
    return buf.append(')').append(';');
  }

  @Override
  public StringBuilder visit(ForStatement forStmt, StringBuilder buf) {
    buf = buf.append("for ").append('(');
    buf = visit(forStmt.init, buf).append(';');
    buf = visit(forStmt.cond, buf).append(';');
    buf = visit(forStmt.update, buf);
    buf = buf.append(')');
    buf = visit(forStmt.stmt, buf);
    return buf;
  }

  @Override
  public StringBuilder visit(TryStatement tryStmt, StringBuilder buf) {
    buf = buf.append("try ");
    buf = visit(tryStmt.tryBlock, buf);
    for(Triad<CtClass, String, Block> c : tryStmt.getCatchBlocks()) {
      buf = buf.append("catch ").append('(');
      buf = buf.append(c._1.getName()).append(' ').append(c._2);
      buf = buf.append(')');
      buf = visit(c._3, buf);
    }
    if (tryStmt.hasFinallyBlock()) {
      buf = buf.append("finally ");
      buf = visit(tryStmt.getFinallyBlock(), buf);
    }
    return buf;
  }

  @Override
  public StringBuilder visit(ThrowStatement throwStmt, StringBuilder buf) {
    buf = buf.append("throw ");
    buf = visit(throwStmt.e, buf);
    return buf.append(';');
  }

  @Override
  public StringBuilder visit(BreakStatement breakStmt, StringBuilder buf) {
    return buf.append("break").append(';');
  }

  @Override
  public StringBuilder visit(ContinueStatement continueStmt, StringBuilder buf) {
    return buf.append("continue").append(';');
  }

  @Override
  public StringBuilder visit(ReturnStatement returnStmt, StringBuilder buf) {
    if (returnStmt.value != null) {
      buf = buf.append("return ");
      buf = visit(returnStmt.value, buf);
      return buf.append(';');
    }
    else return buf.append("return;");
  }

  @Override
  public StringBuilder visit(ExpressionStatement exprStmt, StringBuilder buf) {
    buf = visit(exprStmt.expr, buf);
    return buf.append(';');
  }

  public StringBuilder visit(Expression expr, StringBuilder buf) {
    return expr.accept(this, buf);
  }

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
    CtClass t = newArray.type;
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
  public StringBuilder visit(ArrayInitializer arrayInitializer, StringBuilder buf) {
    buf = buf.append("new ").append(arrayInitializer.type.getName()).append('{');
    if (! arrayInitializer.expressions.isEmpty()) {
      buf = visit(arrayInitializer.expressions.get(0), buf);
      for (int i = 1; i < arrayInitializer.expressions.size(); i++) {
        buf = buf.append(',');
        buf = visit(arrayInitializer.expressions.get(i), buf);
      }
    }
    buf = buf.append('}');
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
    CtClass type = castExpr.type;
    if (type == CtClass.voidType) return visit(castExpr.expr, buf);

    buf = buf.append('(').append('(').append(type.getName()).append(')');
    buf = visit(castExpr.expr, buf);
    buf = buf.append(')');
    return buf;
  }

  @Override
  public StringBuilder visit(VariableArguments operands, StringBuilder buf) {
    buf = buf.append("new ").append(operands.type.getName());
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
  public StringBuilder visit(TypeLiteral typeLiteral, StringBuilder buf) {
    return buf.append("new proteaj.lang.Type(").append(typeLiteral.cls.getName()).append(".class)");
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
      if (operator == BinaryOperator.getInstanceOfOperator()) {
        assert operands.get(1) instanceof TypeLiteral;
        buf = visit(operands.get(0), buf);
        buf = buf.append(" instanceof ");
        buf = buf.append(((TypeLiteral)operands.get(1)).cls.getName());
      }
      else {
        String name = ((BinaryOperator) operator).operator;
        buf = visit(operands.get(0), buf);
        buf = buf.append(' ').append(name).append(' ');
        buf = visit(operands.get(1), buf);
      }
    }
    else {
      assert false;
      throw new RuntimeException("unknown primitive operator");
    }
    buf = buf.append(')');
    return buf;
  }

  private JavassistCodeGenerator() {}
}
