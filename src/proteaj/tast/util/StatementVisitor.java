package proteaj.tast.util;

import proteaj.tast.*;

public abstract class StatementVisitor<T> {
  public T visit (Statement stmt, T t) { return stmt.accept(this, t); }

  public abstract T visit (Block block, T t);
  public abstract T visit (ThisConstructorCall thisStmt, T t);
  public abstract T visit (SuperConstructorCall superStmt, T t);
  public abstract T visit (LocalVarDeclStatement localDecl, T t);
  public abstract T visit (IfStatement ifStmt, T t);
  public abstract T visit (WhileStatement whileStmt, T t);
  public abstract T visit (ForStatement forStmt, T t);
  public abstract T visit (TryStatement tryStmt, T t);
  public abstract T visit (ThrowStatement throwStmt, T t);
  public abstract T visit (ReturnStatement returnStmt, T t);
  public abstract T visit (ExpressionStatement exprStmt, T t);
}
