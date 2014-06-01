package proteaj.tast.util;

import proteaj.tast.*;

import java.util.*;
import java.util.stream.Collectors;

import javassist.*;

public class TreeTranslator implements StatementVisitor<Statement>, ExpressionVisitor<Expression>  {
  public ClassDeclaration translate(ClassDeclaration declaration) {
    ClassDeclaration ret = new ClassDeclaration(declaration.clazz, declaration.filePath);

    for (MethodDeclaration method       : declaration.getMethods())       ret.addMethod(translate(method));
    for (ConstructorDeclaration cons    : declaration.getConstructors())  ret.addConstructor(translate(cons));
    for (FieldDeclaration field         : declaration.getFields())        ret.addField(translate(field));
    for (DefaultValueDefinition def     : declaration.getDefaultValues()) ret.addDefaultValue(translate(def));
    for (ClassInitializerDefinition def : declaration.getInitializers())  ret.addClassInitializer(translate(def));

    return ret;
  }

  public MethodDeclaration translate(MethodDeclaration declaration) {
    return new MethodDeclaration(declaration.method, translate(declaration.body));
  }

  public ConstructorDeclaration translate(ConstructorDeclaration declaration) {
    return new ConstructorDeclaration(declaration.constructor, translate(declaration.body));
  }

  public FieldDeclaration translate(FieldDeclaration declaration) {
    return new FieldDeclaration(declaration.field, translate(declaration.body));
  }

  public DefaultValueDefinition translate(DefaultValueDefinition definition) {
    return new DefaultValueDefinition(definition.method, translate(definition.body));
  }

  public ClassInitializerDefinition translate(ClassInitializerDefinition definition) {
    return new ClassInitializerDefinition(definition.clIni, translate(definition.body));
  }

  public MethodBody translate(MethodBody body) {
    return new MethodBody(translate(body.block));
  }

  public ConstructorBody translate(ConstructorBody body) {
    return new ConstructorBody(translate(body.block));
  }

  public FieldBody translate(FieldBody body) {
    return new FieldBody(translate(body.expr));
  }

  public DefaultValue translate(DefaultValue body) {
    return new DefaultValue(translate(body.expr));
  }

  public ClassInitializer translate(ClassInitializer body) {
    return new ClassInitializer(translate(body.block));
  }

  public Statement translate(Statement statement) {
    return statement.accept(this, null);
  }

  public Block translate(Block block) {
    return new Block(translateStmts(block.getStatements()));
  }

  public Statement translate(ThisConstructorCall thisStmt) {
    return new ThisConstructorCall(thisStmt.constructor, translateExprs(thisStmt.args));
  }

  public Statement translate(SuperConstructorCall superStmt) {
    return new SuperConstructorCall(superStmt.constructor, translateExprs(superStmt.args));
  }

  public Statement translate(LocalsDeclStatement local) {
    return new LocalsDeclStatement(translate(local.localsDecl));
  }

  public Statement translate(IfStatement ifStmt) {
    if (ifStmt.elseStmt == null) return new IfStatement(translate(ifStmt.condition), translate(ifStmt.thenStmt));
    else return new IfStatement(translate(ifStmt.condition), translate(ifStmt.thenStmt), translate(ifStmt.elseStmt));
  }

  public Statement translate(SwitchStatement switchStmt) {
    return new SwitchStatement(translate(switchStmt.expr), switchStmt.cases.stream().map(this::translate).collect(Collectors.toList()));
  }

  public CaseBlock translate(CaseBlock caseBlock) {
    if (caseBlock.isDefault()) return new CaseBlock(translateStmts(caseBlock.stmts));
    else return new CaseBlock(translate(caseBlock.getLabel()), translateStmts(caseBlock.stmts));
  }

  public Statement translate(WhileStatement whileStmt) {
    return new WhileStatement(translate(whileStmt.condition), translate(whileStmt.stmt));
  }

  public Statement translate(DoWhileStatement doWhileStmt) {
    return new DoWhileStatement(translate(doWhileStmt.stmt), translate(doWhileStmt.condition));
  }

  public Statement translate(ForStatement forStmt) {
    return new ForStatement(translate(forStmt.init), translate(forStmt.cond), translate(forStmt.update), translate(forStmt.stmt));
  }

  public Statement translate(TryStatement tryStmt) {
    List<CatchBlock> catches = tryStmt.catchBlocks.stream().map(c -> new CatchBlock(c.clazz, c.name, translate(c.block))).collect(Collectors.toList());
    if (! tryStmt.hasFinallyBlock()) return new TryStatement(translate(tryStmt.tryBlock), catches);
    else return new TryStatement(translate(tryStmt.tryBlock), catches, translate(tryStmt.getFinallyBlock()));
  }

  public Statement translate(ThrowStatement throwStmt) {
    return new ThrowStatement(translate(throwStmt.e));
  }

  public Statement translate(BreakStatement breakStmt) {
    return breakStmt;
  }

  public Statement translate(ContinueStatement continueStmt) {
    return continueStmt;
  }

  public Statement translate(ReturnStatement returnStmt) {
    if (returnStmt.value == null) return returnStmt;
    else return new ReturnStatement(translate(returnStmt.value));
  }

  public Statement translate(ExpressionStatement exprStmt) {
    return new ExpressionStatement(translate(exprStmt.expr));
  }

  public Statement translate (SynchronizedStatement syncStmt) {
    return new SynchronizedStatement(translate(syncStmt.expr), translate(syncStmt.block));
  }

  public Expression translate(Expression expression) {
    return expression.accept(this, null);
  }

  public Expression translate(Operation operation) {
    return new Operation(operation.operator, translateExprs(operation.operands));
  }

  public LocalsDecl translate(LocalsDecl locals) {
    return new LocalsDecl(locals.isFinal, locals.type, locals.locals.stream().map(this::translate).collect(Collectors.toList()));
  }

  public LocalsDecl.LocalDecl translate(LocalsDecl.LocalDecl local) {
    if (local.val == null) return new LocalsDecl.LocalDecl(local.type, local.name, local.dim);
    else return new LocalsDecl.LocalDecl(local.type, local.name, local.dim, translate(local.val));
  }

  public Expression translate(ExpressionList list) {
    return new ExpressionList(translateExprs(list.exprs));
  }

  public Expression translate(AssignExpression assign) {
    return new AssignExpression(translate(assign.ref), translate(assign.val));
  }

  public Expression translate(TernaryIfExpression tif) {
    return new TernaryIfExpression(tif.type, translate(tif.condition), translate(tif.thenExpr), translate(tif.elseExpr));
  }

  public Expression translate(MethodCall methodCall) {
    try { return new MethodCall(translate(methodCall.expr), methodCall.method, translateExprs(methodCall.args)); }
    catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  public Expression translate(StaticMethodCall methodCall) {
    try { return new StaticMethodCall(methodCall.method, translateExprs(methodCall.args)); }
    catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  public Expression translate(FieldAccess fieldAccess) {
    try { return new FieldAccess(translate(fieldAccess.expr), fieldAccess.field); }
    catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  public Expression translate(StaticFieldAccess fieldAccess) {
    return fieldAccess;
  }

  public Expression translate(NewExpression newExpression) {
    return new NewExpression(newExpression.constructor, translateExprs(newExpression.args));
  }

  public Expression translate(NewArrayExpression newArray) {
    return new NewArrayExpression(newArray.type, translateExprs(newArray.args));
  }

  public Expression translate(ArrayAccess arrayAccess) {
    try { return new ArrayAccess(translate(arrayAccess.array), translate(arrayAccess.index)); }
    catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  public Expression translate(ArrayInitializer arrayInitializer) {
    return new ArrayInitializer(arrayInitializer.type, translateExprs(arrayInitializer.expressions));
  }

  public Expression translate(ArrayLength arrayLength) {
    return new ArrayLength(translate(arrayLength.array));
  }

  public Expression translate(ThisExpression thisExpression) {
    return thisExpression;
  }

  public Expression translate(SuperExpression superExpression) {
    return superExpression;
  }

  public Expression translate(ParamAccess paramAccess) {
    return paramAccess;
  }

  public Expression translate(LocalVariable local) {
    return local;
  }

  public Expression translate(CastExpression cast) {
    return new CastExpression(cast.type, translate(cast.expr));
  }

  public Expression translate(VariableArguments varArgs) {
    return new VariableArguments(translateExprs(varArgs.args), varArgs.type);
  }

  public Expression translate(StringLiteral stringLiteral) {
    return stringLiteral;
  }

  public Expression translate(CharLiteral charLiteral) {
    return charLiteral;
  }

  public Expression translate(IntLiteral intLiteral) {
    return intLiteral;
  }

  public Expression translate(BooleanLiteral booleanLiteral) {
    return booleanLiteral;
  }

  public Expression translate(ClassLiteral classLiteral) {
    return classLiteral;
  }

  public Expression translate(TypeLiteral typeLiteral) {
    return typeLiteral;
  }

  public Expression translate(NullLiteral nullLiteral) {
    return nullLiteral;
  }

  /* utility */

  private List<Expression> translateExprs(List<Expression> list) {
    return list.stream().map(this::translate).collect(Collectors.toList());
  }

  private List<Statement> translateStmts(List<Statement> list) {
    return list.stream().map(this::translate).collect(Collectors.toList());
  }

  /* visitor */

  @Override
  public final Expression visit(Operation operation, Expression expression) {
    return translate(operation);
  }

  @Override
  public final Expression visit(LocalsDecl local, Expression expression) {
    return translate(local);
  }

  @Override
  public final Expression visit(ExpressionList list, Expression expression) {
    return translate(list);
  }

  @Override
  public final Expression visit(AssignExpression assign, Expression expression) {
    return translate(assign);
  }

  @Override
  public final Expression visit(TernaryIfExpression tif, Expression expression) { return translate(tif); }

  @Override
  public final Expression visit(MethodCall methodCall, Expression expression) {
    return translate(methodCall);
  }

  @Override
  public final Expression visit(StaticMethodCall methodCall, Expression expression) {
    return translate(methodCall);
  }

  @Override
  public final Expression visit(FieldAccess fieldAccess, Expression expression) {
    return translate(fieldAccess);
  }

  @Override
  public final Expression visit(StaticFieldAccess fieldAccess, Expression expression) {
    return translate(fieldAccess);
  }

  @Override
  public final Expression visit(NewExpression newExpression, Expression expression) {
    return translate(newExpression);
  }

  @Override
  public final Expression visit(NewArrayExpression newArray, Expression expression) {
    return translate(newArray);
  }

  @Override
  public final Expression visit(ArrayAccess arrayAccess, Expression expression) {
    return translate(arrayAccess);
  }

  @Override
  public final Expression visit(ArrayInitializer arrayInitializer, Expression expression) { return translate(arrayInitializer); }

  @Override
  public final Expression visit(ArrayLength arrayLength, Expression expression) {
    return translate(arrayLength);
  }

  @Override
  public final Expression visit(ThisExpression thisExpr, Expression expression) {
    return translate(thisExpr);
  }

  @Override
  public final Expression visit(SuperExpression superExpr, Expression expression) {
    return translate(superExpr);
  }

  @Override
  public final Expression visit(ParamAccess paramAccess, Expression expression) {
    return translate(paramAccess);
  }

  @Override
  public final Expression visit(LocalVariable local, Expression expression) {
    return translate(local);
  }

  @Override
  public final Expression visit(CastExpression castExpr, Expression expression) {
    return translate(castExpr);
  }

  @Override
  public final Expression visit(VariableArguments varArgs, Expression expression) {
    return translate(varArgs);
  }

  @Override
  public final Expression visit(StringLiteral stringLiteral, Expression expression) {
    return translate(stringLiteral);
  }

  @Override
  public final Expression visit(CharLiteral charLiteral, Expression expression) {
    return translate(charLiteral);
  }

  @Override
  public final Expression visit(IntLiteral intLiteral, Expression expression) {
    return translate(intLiteral);
  }

  @Override
  public final Expression visit(BooleanLiteral booleanLiteral, Expression expression) { return translate(booleanLiteral); }

  @Override
  public final Expression visit(ClassLiteral classLiteral, Expression expression) {
    return translate(classLiteral);
  }

  @Override
  public final Expression visit(TypeLiteral typeLiteral, Expression expression) {
    return translate(typeLiteral);
  }

  @Override
  public final Expression visit(NullLiteral nullLiteral, Expression expression) {
    return translate(nullLiteral);
  }

  @Override
  public final Statement visit(Block block, Statement statement) {
    return translate(block);
  }

  @Override
  public final Statement visit(ThisConstructorCall thisStmt, Statement statement) {
    return translate(thisStmt);
  }

  @Override
  public final Statement visit(SuperConstructorCall superStmt, Statement statement) {
    return translate(superStmt);
  }

  @Override
  public final Statement visit(LocalsDeclStatement localDecl, Statement statement) {
    return translate(localDecl);
  }

  @Override
  public final Statement visit(IfStatement ifStmt, Statement statement) {
    return translate(ifStmt);
  }

  @Override
  public final Statement visit(SwitchStatement switchStmt, Statement statement) { return translate(switchStmt); }

  @Override
  public final Statement visit(WhileStatement whileStmt, Statement statement) {
    return translate(whileStmt);
  }

  @Override
  public final Statement visit(DoWhileStatement doWhileStmt, Statement statement) {
    return translate(doWhileStmt);
  }

  @Override
  public final Statement visit(ForStatement forStmt, Statement statement) {
    return translate(forStmt);
  }

  @Override
  public final Statement visit(TryStatement tryStmt, Statement statement) {
    return translate(tryStmt);
  }

  @Override
  public final Statement visit(ThrowStatement throwStmt, Statement statement) {
    return translate(throwStmt);
  }

  @Override
  public final Statement visit(BreakStatement breakStmt, Statement statement) {
    return translate(breakStmt);
  }

  @Override
  public final Statement visit(ContinueStatement continueStmt, Statement statement) {
    return translate(continueStmt);
  }

  @Override
  public final Statement visit(ReturnStatement returnStmt, Statement statement) {
    return translate(returnStmt);
  }

  @Override
  public final Statement visit(ExpressionStatement exprStmt, Statement statement) {
    return translate(exprStmt);
  }

  @Override
  public Statement visit(SynchronizedStatement syncStmt, Statement statement) { return translate(syncStmt); }
}
