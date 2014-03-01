package proteaj.codegen.javassist;

import proteaj.tast.*;
import proteaj.tast.util.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class StatementCodeGenerator extends StatementVisitor<StringBuilder> {
  public static final StatementCodeGenerator instance = new StatementCodeGenerator();

  public StringBuilder codeGen (Statement stmt, StringBuilder buf) {
    return visit(stmt, buf);
  }

  @Override
  public StringBuilder visit(Block block, StringBuilder buf) {
    buf = buf.append('{');
    for (Statement s : block.getStatements()) buf = visit(s, buf.append('\n'));
    buf = buf.append('}');
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
      buf = buf.append(c.getFirst().getName()).append(' ').append(c.getSecond());
      buf = buf.append(')');
      buf = visit(c.getThird(), buf);
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

  private StringBuilder visit(Expression expr, StringBuilder buf) {
    return ExpressionCodeGenerator.instance.visit(expr, buf);
  }

  private StatementCodeGenerator() {}
}
