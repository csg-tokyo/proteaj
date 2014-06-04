package proteaj.codegen;

import proteaj.ir.IROperator;
import proteaj.ir.primitive.*;
import proteaj.tast.*;
import proteaj.tast.util.*;
import proteaj.util.Modifiers;

import java.util.*;
import javassist.*;

import static proteaj.util.Escape.escape;
import static proteaj.util.Escape.unicodeEscape;

public class JavaCodeGenerator implements ExpressionVisitor<CodeBuffer>, StatementVisitor<CodeBuffer> {
  public static String generateJavaCode (ClassDeclaration clazz) {
    return new JavaCodeGenerator(clazz.clazz).visit(clazz, new CodeBuffer()).toString();
  }

  public String codeGen (MethodBody body) {
    return visit(body, new CodeBuffer()).toString();
  }

  public String codeGen (ConstructorBody body) {
    return visit(body, new CodeBuffer()).toString();
  }

  public String codeGen (FieldBody body) {
    return visit(body, new CodeBuffer()).toString();
  }

  public String codeGen (DefaultValue body) {
    return visit(body, new CodeBuffer()).toString();
  }

  public String codeGen (ClassInitializer body) {
    return visit(body, new CodeBuffer()).toString();
  }

  public CodeBuffer visit (ClassDeclaration clazz, CodeBuffer buf) {
    buf = buf.append("package ").append(clazz.clazz.getPackageName()).append(';').newLine();

    buf = buf.append(Modifiers.toString(clazz.clazz.getModifiers()));

    if (clazz.clazz.isInterface()) {
      buf = buf.append(" interface ").append(clazz.clazz.getSimpleName());

      try {
        CtClass[] interfaces = clazz.clazz.getInterfaces();
        if (interfaces != null && interfaces.length != 0) {
          buf = buf.append(" extends ").append(interfaces[0]);
          for (int i = 1; i < interfaces.length; i++) buf = buf.append(", ").append(interfaces[i]);
        }
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    else {
      buf = buf.append(" class ").append(clazz.clazz.getSimpleName());
      try {
        CtClass sup = clazz.clazz.getSuperclass();
        if (sup != null) buf = buf.append(" extends ").append(sup);
      } catch (NotFoundException e) {
        e.printStackTrace();
      }

      try {
        CtClass[] interfaces = clazz.clazz.getInterfaces();
        if (interfaces != null && interfaces.length != 0) {
          buf = buf.append(" implements ").append(interfaces[0]);
          for (int i = 1; i < interfaces.length; i++) buf = buf.append(", ").append(interfaces[i]);
        }
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    buf = buf.append(" {").indent().newLine();

    for (CtField field : clazz.getDeclaredFields_Ordered()) {
      FieldDeclaration decl = clazz.getField(field);

      if (decl != null) buf = visit(decl, buf.newLine());
      else try {
        buf = buf.newLine();
        buf = buf.append(Modifiers.toString(field.getModifiers())).append(' ');
        buf = buf.append(field.getType()).append(' ');
        buf = buf.append(field.getName()).append(';');
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    for (CtMethod method : clazz.clazz.getDeclaredMethods()) {
      MethodDeclaration decl = clazz.getMethod(method);

      if (decl != null) buf = visit(decl, buf.newLine());
      else try {
        buf = buf.newLine();
        buf = buf.append(Modifiers.toString(method.getModifiers())).append(' ');
        buf = buf.append(method.getReturnType()).append(' ');
        buf = buf.append(method.getName()).append(' ');
        buf = codeGenParams(method.getParameterTypes(), buf);
        buf = codeGenThrows(method.getExceptionTypes(), buf).append(';');
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    for (DefaultValueDefinition dvDef : clazz.getDefaultValues()) buf = visit(dvDef, buf.newLine());
    for (ConstructorDeclaration con : clazz.getConstructors())    buf = visit(con, buf.newLine());

    for (ClassInitializerDefinition ini : clazz.getInitializers()) {
      buf = buf.newLine();
      buf = buf.append("static ").indent();
      buf = visit(ini.body, buf.newLine());
      buf = buf.unindent().newLine().newLine();
    }

    buf = buf.unindent().newLine().append("}").newLine();
    return buf;
  }

  public CodeBuffer visit (ConstructorDeclaration constructor, CodeBuffer buf) {
    try {
      buf = buf.append(Modifiers.toString(constructor.constructor.getModifiers())).append(' ');
      buf = buf.append(constructor.constructor.getName()).append(' ');
      buf = codeGenParams(constructor.constructor.getParameterTypes(), buf);
      buf = codeGenThrows(constructor.constructor.getExceptionTypes(), buf);
      buf = visit(constructor.body, buf.append(' '));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return buf;
  }

  public CodeBuffer visit (MethodDeclaration method, CodeBuffer buf) {
    try {
      buf = buf.append(Modifiers.toString(method.method.getModifiers())).append(' ');
      buf = buf.append(method.method.getReturnType()).append(' ');
      buf = buf.append(method.method.getName()).append(' ');
      buf = codeGenParams(method.method.getParameterTypes(), buf);
      buf = codeGenThrows(method.method.getExceptionTypes(), buf);
      buf = visit(method.body, buf.append(' '));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return buf;
  }

  public CodeBuffer visit (FieldDeclaration field, CodeBuffer buf) {
    try {
      buf = buf.append(Modifiers.toString(field.field.getModifiers())).append(' ');
      buf = buf.append(field.field.getType()).append(' ');
      buf = buf.append(field.field.getName());
      buf = buf.append(" = ");
      buf = visit(field.body, buf);
      buf = buf.append(';');
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return buf;
  }

  public CodeBuffer visit (DefaultValueDefinition dvDef, CodeBuffer buf) {
    try {
      buf = buf.append(Modifiers.toString(dvDef.method.getModifiers())).append(' ');
      buf = buf.append(dvDef.method.getReturnType()).append(' ');
      buf = buf.append(dvDef.method.getName()).append(' ');
      buf = codeGenParams(dvDef.method.getParameterTypes(), buf);
      buf = codeGenThrows(dvDef.method.getExceptionTypes(), buf);
      buf = visit(dvDef.body, buf.append(' '));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return buf;
  }

  private CodeBuffer codeGenParams(CtClass[] types, CodeBuffer buf) {
    buf = buf.append('(');

    if (types != null && types.length > 0) {
      buf = buf.append(types[0]).append(" $1");
      for (int i = 1; i < types.length; i++) buf = buf.append(", ").append(types[i]).append(" $").append(i + 1);
    }

    return buf.append(')');
  }

  private CodeBuffer codeGenThrows(CtClass[] types, CodeBuffer buf) {
    if (types != null && types.length > 0) {
      buf.append(" throws ").append(types[0]);
      for (int i = 1; i < types.length; i++) buf = buf.append(", ").append(types[i]);
    }
    return buf;
  }

  public CodeBuffer visit (MethodBody body, CodeBuffer buf) {
    return visit(body.block, buf);
  }

  public CodeBuffer visit (ConstructorBody body, CodeBuffer buf) {
    return visit(body.block, buf);
  }

  public CodeBuffer visit (FieldBody body, CodeBuffer buf) {
    return visit(body.expr, buf);
  }

  public CodeBuffer visit (DefaultValue body, CodeBuffer buf) {
    buf = buf.append('{').indent().newLine().append("return ");
    buf = visit(body.expr, buf);
    buf = buf.append(';').unindent().newLine().append('}');
    return buf;
  }

  public CodeBuffer visit (ClassInitializer body, CodeBuffer buf) {
    return visit(body.block, buf);
  }

  public CodeBuffer visit(Statement stmt, CodeBuffer buf) {
    return stmt.accept(this, buf);
  }

  @Override
  public CodeBuffer visit(Block block, CodeBuffer buf) {
    buf = buf.append('{').indent();
    for (Statement s : block.getStatements()) buf = visit(s, buf.newLine());
    buf = buf.unindent().newLine().append('}');
    return buf;
  }

  @Override
  public CodeBuffer visit(ThisConstructorCall thisStmt, CodeBuffer buf) {
    buf = buf.append("this").append('(');

    List<Expression> args = thisStmt.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')').append(';');
  }

  @Override
  public CodeBuffer visit(SuperConstructorCall superStmt, CodeBuffer buf) {
    buf = buf.append("super").append('(');

    List<Expression> args = superStmt.args;

    if (! args.isEmpty()) buf = visit(args.get(0), buf);
    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    return buf.append(')').append(';');
  }

  @Override
  public CodeBuffer visit(LocalsDeclStatement localDecl, CodeBuffer buf) {
    buf = visit(localDecl.localsDecl, buf);
    return buf.append(';');
  }

  @Override
  public CodeBuffer visit(IfStatement ifStmt, CodeBuffer buf) {
    buf = buf.append("if ").append('(');
    buf = visit(ifStmt.condition, buf);
    buf = buf.append(')').append(' ');
    buf = visit(ifStmt.thenStmt, buf);
    if (ifStmt.elseStmt != null) {
      buf = buf.append(" else ");
      buf = visit(ifStmt.elseStmt, buf);
    }
    return buf;
  }

  @Override
  public CodeBuffer visit(SwitchStatement switchStmt, CodeBuffer buf) {
    buf = buf.append("switch ").append('(');
    buf = visit(switchStmt.expr, buf);
    buf = buf.append(')').append('{');

    for (CaseBlock caseBlock : switchStmt.cases) {
      if (caseBlock.isDefault()) buf = buf.newLine().append("default :");
      else {
        buf = buf.newLine().append("case ");
        buf = visit(caseBlock.getLabel(), buf).append(" :");
      }

      for (Statement stmt : caseBlock.stmts) buf = visit(stmt, buf.newLine());
    }

    return buf.newLine().append('}');
  }

  @Override
  public CodeBuffer visit(WhileStatement whileStmt, CodeBuffer buf) {
    buf = buf.append("while ").append('(');
    buf = visit(whileStmt.condition, buf);
    buf = buf.append(')');
    buf = visit(whileStmt.stmt, buf);
    return buf;
  }

  @Override
  public CodeBuffer visit(DoWhileStatement doWhileStmt, CodeBuffer buf) {
    buf = buf.append("do ");
    buf = visit(doWhileStmt.stmt, buf);
    buf = buf.append("while ").append('(');
    buf = visit(doWhileStmt.condition, buf);
    return buf.append(')').append(';');
  }

  @Override
  public CodeBuffer visit(ForStatement forStmt, CodeBuffer buf) {
    buf = buf.append("for ").append('(');
    buf = visit(forStmt.init, buf).append(';');
    buf = visit(forStmt.cond, buf).append(';');
    buf = visit(forStmt.update, buf);
    buf = buf.append(')');
    buf = visit(forStmt.stmt, buf);
    return buf;
  }

  @Override
  public CodeBuffer visit(TryStatement tryStmt, CodeBuffer buf) {
    buf = buf.append("try ");
    buf = visit(tryStmt.tryBlock, buf);
    for(CatchBlock c : tryStmt.catchBlocks) {
      buf = buf.append(" catch ").append('(');
      buf = buf.append(c.clazz).append(' ').append(c.name);
      buf = buf.append(')');
      buf = visit(c.block, buf);
    }
    if (tryStmt.hasFinallyBlock()) {
      buf = buf.append(" finally ");
      buf = visit(tryStmt.getFinallyBlock(), buf);
    }
    return buf;
  }

  @Override
  public CodeBuffer visit(ThrowStatement throwStmt, CodeBuffer buf) {
    buf = buf.append("throw ");
    buf = visit(throwStmt.e, buf);
    return buf.append(';');
  }

  @Override
  public CodeBuffer visit(BreakStatement breakStmt, CodeBuffer buf) {
    return buf.append("break").append(';');
  }

  @Override
  public CodeBuffer visit(ContinueStatement continueStmt, CodeBuffer buf) {
    return buf.append("continue").append(';');
  }

  @Override
  public CodeBuffer visit(ReturnStatement returnStmt, CodeBuffer buf) {
    if (returnStmt.value != null) {
      buf = buf.append("return ");
      buf = visit(returnStmt.value, buf);
      return buf.append(';');
    }
    else return buf.append("return;");
  }

  @Override
  public CodeBuffer visit(ExpressionStatement exprStmt, CodeBuffer buf) {
    buf = visit(exprStmt.expr, buf);
    return buf.append(';');
  }

  @Override
  public CodeBuffer visit(SynchronizedStatement syncStmt, CodeBuffer buf) {
    buf = buf.append("synchronized ").append('(');
    buf = visit(syncStmt.expr, buf);
    buf = buf.append(')');
    buf = visit(syncStmt.block, buf);
    return buf;
  }

  public CodeBuffer visit(Expression expr, CodeBuffer buf) {
    return expr.accept(this, buf);
  }

  @Override
  public CodeBuffer visit(Operation operation, CodeBuffer buf) {
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
  public CodeBuffer visit(LocalsDecl locals, CodeBuffer buf) {
    if (locals.isFinal) buf = buf.append("final ");
    buf = buf.append(locals.type).append(' ');

    for (int i = 0; i < locals.locals.size(); i++) {
      LocalsDecl.LocalDecl local = locals.locals.get(i);

      if (i != 0) buf = buf.append(',');
      buf = buf.append(local.name);

      for (int n = 0; n < local.dim; n++) buf = buf.append("[]");

      if (local.val != null) {
        buf = buf.append(" = ");
        buf = visit(local.val, buf);
      }
    }
    return buf;
  }

  @Override
  public CodeBuffer visit(ExpressionList list, CodeBuffer buf) {
    if (list.exprs.isEmpty()) return buf;

    buf = visit(list.exprs.get(0), buf);
    for (int i = 1; i < list.exprs.size(); i++) {
      buf = visit(list.exprs.get(i), buf.append(','));
    }
    return buf;
  }

  @Override
  public CodeBuffer visit(AssignExpression assign, CodeBuffer buf) {
    buf = visit(assign.ref, buf);
    buf = buf.append(" = ");
    buf = buf.append('(');
    buf = visit(assign.val, buf);
    buf = buf.append(')');
    return buf;
  }

  @Override
  public CodeBuffer visit(TernaryIfExpression tif, CodeBuffer buf) {
    buf = visit(tif.condition, buf);
    buf = buf.append('?');
    buf = visit(tif.thenExpr, buf);
    buf = buf.append(':');
    buf = visit(tif.elseExpr, buf);
    return buf;
  }

  @Override
  public CodeBuffer visit(MethodCall methodCall, CodeBuffer buf) {
    buf = visit(methodCall.expr, buf).append('.');
    buf = buf.append(methodCall.method.getName());
    buf = codeGenArgs(methodCall.args, methodCall.method, buf);
    return buf;
  }

  @Override
  public CodeBuffer visit(StaticMethodCall methodCall, CodeBuffer buf) {
    if (! thisClass.equals(methodCall.method.getDeclaringClass())) {
      buf = buf.append(methodCall.method.getDeclaringClass()).append('.');
    }

    buf = buf.append(methodCall.method.getName());
    buf = codeGenArgs(methodCall.args, methodCall.method, buf);

    return buf;
  }

  @Override
  public CodeBuffer visit(FieldAccess fieldAccess, CodeBuffer buf) {
    buf = visit(fieldAccess.expr, buf).append('.');
    buf = buf.append(fieldAccess.field.getName());
    return buf;
  }

  @Override
  public CodeBuffer visit(StaticFieldAccess fieldAccess, CodeBuffer buf) {
    if (! thisClass.equals(fieldAccess.field.getDeclaringClass())) {
      buf = buf.append(fieldAccess.field.getDeclaringClass()).append('.');
    }

    buf = buf.append(fieldAccess.field.getName());
    return buf;
  }

  @Override
  public CodeBuffer visit(NewExpression newExpression, CodeBuffer buf) {
    buf = buf.append("new ").append(newExpression.constructor.getDeclaringClass());
    buf = codeGenArgs(newExpression.args, newExpression.constructor, buf);
    return buf;
  }

  @Override
  public CodeBuffer visit(NewArrayExpression newArray, CodeBuffer buf) {
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
    buf = buf.append(t);

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
  public CodeBuffer visit(ArrayAccess arrayAccess, CodeBuffer buf) {
    buf = visit(arrayAccess.array, buf);
    buf = buf.append('[');
    buf = visit(arrayAccess.index, buf);
    buf = buf.append(']');
    return buf;
  }

  @Override
  public CodeBuffer visit(ArrayInitializer arrayInitializer, CodeBuffer buf) {
    buf = buf.append("new ").append(arrayInitializer.type).append('{');
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
  public CodeBuffer visit(ArrayLength arrayLength, CodeBuffer buf) {
    return visit(arrayLength.array, buf).append(".length");
  }

  @Override
  public CodeBuffer visit(ThisExpression thisExpr, CodeBuffer buf) {
    return buf.append("this");
  }

  @Override
  public CodeBuffer visit(SuperExpression superExpr, CodeBuffer buf) {
    return buf.append("super");
  }

  @Override
  public CodeBuffer visit(ParamAccess paramAccess, CodeBuffer buf) {
    return buf.append("$").append(paramAccess.index + 1);
  }

  @Override
  public CodeBuffer visit(LocalVariable local, CodeBuffer buf) {
    return buf.append(local.name);
  }

  @Override
  public CodeBuffer visit(CastExpression castExpr, CodeBuffer buf) {
    CtClass type = castExpr.type;
    if (type == CtClass.voidType) return visit(castExpr.expr, buf);

    buf = buf.append('(').append('(').append(type).append(')').append('(');
    buf = visit(castExpr.expr, buf);
    buf = buf.append(')').append(')');
    return buf;
  }

  @Override
  public CodeBuffer visit(VariableArguments operands, CodeBuffer buf) {
    buf = buf.append("new ").append(operands.type);
    buf = buf.append('{');

    List<Expression> args = operands.args;
    if (! args.isEmpty()) buf = visit(args.get(0), buf);

    for (int i = 1; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    buf = buf.append('}');
    return buf;
  }

  @Override
  public CodeBuffer visit(StringLiteral string, CodeBuffer buf) {
    return buf.append('"').append(escape(string.str)).append('"');
  }

  @Override
  public CodeBuffer visit(CharLiteral ch, CodeBuffer buf) {
    return buf.append('\'').append(unicodeEscape(ch.val)).append('\'');
  }

  @Override
  public CodeBuffer visit(IntLiteral i, CodeBuffer buf) {
    if (Integer.MIN_VALUE <= i.val && i.val <= Integer.MAX_VALUE) return buf.append(Long.toString(i.val));
    else return buf.append(Long.toString(i.val)).append('L');
  }

  @Override
  public CodeBuffer visit(FloatLiteral floatLiteral, CodeBuffer buf) {
    return buf.append(floatLiteral.val).append('f');
  }

  @Override
  public CodeBuffer visit(DoubleLiteral doubleLiteral, CodeBuffer buf) {
    return buf.append(doubleLiteral.val);
  }

  @Override
  public CodeBuffer visit(BooleanLiteral bool, CodeBuffer buf) {
    return buf.append(bool.val);
  }

  @Override
  public CodeBuffer visit(ClassLiteral clazz, CodeBuffer buf) {
    return buf.append(clazz.cls).append(".class");
  }

  @Override
  public CodeBuffer visit(TypeLiteral typeLiteral, CodeBuffer buf) {
    return buf.append("new proteaj.lang.Type(").append(typeLiteral.cls).append(".class)");
  }

  @Override
  public CodeBuffer visit(NullLiteral nul, CodeBuffer buf) {
    return buf.append("null");
  }

  private CodeBuffer codeGenArgs (List<Expression> args, CtBehavior behavior, CodeBuffer buf) {
    final CtClass[] types;
    try {
      types = behavior.getParameterTypes();
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }

    assert args.size() == types.length;

    buf = buf.append('(');

    if (types.length != 0) {
      buf = codeGenArg(args.get(0), types[0], buf);

      for (int i = 1; i < types.length; i++) {
        buf = buf.append(", ");
        buf = codeGenArg(args.get(i), types[i], buf);
      }
    }

    buf = buf.append(')');
    return buf;
  }

  private CodeBuffer codeGenArg (Expression arg, CtClass type, CodeBuffer buf) {
    buf = buf.append('(').append(type).append(')');
    buf = buf.append('(');
    buf = visit(arg, buf);
    buf = buf.append(')');
    return buf;
  }

  private CodeBuffer codeGenPrimitiveOperator (PrimitiveOperator operator, List<Expression> operands, CodeBuffer buf) {
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
        buf = buf.append('(');
        buf = visit(operands.get(0), buf);
        buf = buf.append(" instanceof ");
        buf = buf.append(((TypeLiteral)operands.get(1)).cls);
        buf = buf.append(')');
      }
      else {
        String name = ((BinaryOperator) operator).operator;
        buf = buf.append('(');
        buf = visit(operands.get(0), buf);
        buf = buf.append(')');
        buf = buf.append(' ').append(name).append(' ');
        buf = buf.append('(');
        buf = visit(operands.get(1), buf);
        buf = buf.append(')');
      }
    }
    else {
      assert false;
      throw new RuntimeException("unknown primitive operator");
    }
    return buf;
  }

  public JavaCodeGenerator (CtClass clazz) {
    this.thisClass = clazz;
  }

  private CtClass thisClass;
}
