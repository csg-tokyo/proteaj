package proteaj.tast.util;

import proteaj.tast.*;

public abstract class ExpressionVisitor<T> {
  public T visit (Expression e, T t) { return e.accept(this, t); }

  public abstract T visit (Operation operation, T t);
  public abstract T visit (LocalVarDecl local, T t);
  public abstract T visit (ExpressionList list, T t);
  public abstract T visit (AssignExpression assign, T t);
  public abstract T visit (MethodCall methodCall, T t);
  public abstract T visit (StaticMethodCall methodCall, T t);
  public abstract T visit (FieldAccess fieldAccess, T t);
  public abstract T visit (StaticFieldAccess fieldAccess, T t);
  public abstract T visit (NewExpression newExpression, T t);
  public abstract T visit (NewArrayExpression newArray, T t);
  public abstract T visit (ArrayAccess arrayAccess, T t);
  public abstract T visit (ArrayLength arrayLength, T t);
  public abstract T visit (ThisExpression thisExpr, T t);
  public abstract T visit (SuperExpression superExpr, T t);
  public abstract T visit (ParamAccess paramAccess, T t);
  public abstract T visit (LocalVariable local, T t);
  public abstract T visit (CastExpression castExpr, T t);
  public abstract T visit (VariableArguments varArgs, T t);

  public abstract T visit (StringLiteral stringLiteral, T t);
  public abstract T visit (CharLiteral charLiteral, T t);
  public abstract T visit (IntLiteral intLiteral, T t);
  public abstract T visit (BooleanLiteral booleanLiteral, T t);
  public abstract T visit (ClassLiteral classLiteral, T t);
  public abstract T visit (NullLiteral nullLiteral, T t);
}
