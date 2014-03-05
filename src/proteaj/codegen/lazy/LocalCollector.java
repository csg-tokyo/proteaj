package proteaj.codegen.lazy;

import proteaj.tast.*;
import proteaj.tast.util.*;

import java.util.*;

public class LocalCollector implements ExpressionVisitor<Set<Expression>> {
  public static final LocalCollector instance = new LocalCollector();

  public static Set<Expression> collectLocals(Expression e) {
    return instance.visit(e, new HashSet<Expression>());
  }

  public Set<Expression> visit(Expression expression, Set<Expression> localVariables) {
    return expression.accept(this, localVariables);
  }

  @Override
  public Set<Expression> visit(Operation operation, Set<Expression> localVariables) {
    for (Expression e : operation.operands) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(LocalVarDecl local, Set<Expression> localVariables) {
    return visit(local.val, localVariables);
  }

  @Override
  public Set<Expression> visit(ExpressionList list, Set<Expression> localVariables) {
    for (Expression e : list.exprs) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(AssignExpression assign, Set<Expression> localVariables) {
    localVariables = visit(assign.ref, localVariables);
    localVariables = visit(assign.val, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(MethodCall methodCall, Set<Expression> localVariables) {
    localVariables = visit(methodCall.expr, localVariables);
    for (Expression e : methodCall.args) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(StaticMethodCall methodCall, Set<Expression> localVariables) {
    for (Expression e : methodCall.args) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(FieldAccess fieldAccess, Set<Expression> localVariables) {
    return visit(fieldAccess.expr, localVariables);
  }

  @Override
  public Set<Expression> visit(StaticFieldAccess fieldAccess, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(NewExpression newExpression, Set<Expression> localVariables) {
    for (Expression e : newExpression.args) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(NewArrayExpression newArray, Set<Expression> localVariables) {
    for (Expression e : newArray.args) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(ArrayAccess arrayAccess, Set<Expression> localVariables) {
    localVariables = visit(arrayAccess.array, localVariables);
    localVariables = visit(arrayAccess.index, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(ArrayLength arrayLength, Set<Expression> localVariables) {
    return visit(arrayLength.array, localVariables);
  }

  @Override
  public Set<Expression> visit(ThisExpression thisExpr, Set<Expression> localVariables) {
    localVariables.add(thisExpr);
    return localVariables;
  }

  // TODO
  @Override
  public Set<Expression> visit(SuperExpression superExpr, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(ParamAccess paramAccess, Set<Expression> localVariables) {
    localVariables.add(paramAccess);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(LocalVariable local, Set<Expression> localVariables) {
    localVariables.add(local);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(CastExpression castExpr, Set<Expression> localVariables) {
    return visit(castExpr.expr, localVariables);
  }

  @Override
  public Set<Expression> visit(VariableArguments varArgs, Set<Expression> localVariables) {
    for (Expression e : varArgs.args) localVariables = visit(e, localVariables);
    return localVariables;
  }

  @Override
  public Set<Expression> visit(StringLiteral stringLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(CharLiteral charLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(IntLiteral intLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(BooleanLiteral booleanLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(ClassLiteral classLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  @Override
  public Set<Expression> visit(NullLiteral nullLiteral, Set<Expression> localVariables) {
    return localVariables;
  }

  private LocalCollector() {}
}
