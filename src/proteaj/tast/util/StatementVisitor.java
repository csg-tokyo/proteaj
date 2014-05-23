package proteaj.tast.util;

import proteaj.tast.*;

public interface StatementVisitor<T> {
  public abstract T visit (Block block, T t);
  public abstract T visit (ThisConstructorCall thisStmt, T t);
  public abstract T visit (SuperConstructorCall superStmt, T t);
  public abstract T visit (LocalsDeclStatement localDecl, T t);
  public abstract T visit (IfStatement ifStmt, T t);
  public abstract T visit (SwitchStatement switchStmt, T t);
  public abstract T visit (WhileStatement whileStmt, T t);
  public abstract T visit (DoWhileStatement doWhileStmt, T t);
  public abstract T visit (ForStatement forStmt, T t);
  public abstract T visit (TryStatement tryStmt, T t);
  public abstract T visit (ThrowStatement throwStmt, T t);
  public abstract T visit (BreakStatement breakStmt, T t);
  public abstract T visit (ContinueStatement continueStmt, T t);
  public abstract T visit (ReturnStatement returnStmt, T t);
  public abstract T visit (ExpressionStatement exprStmt, T t);
  public abstract T visit (SynchronizedStatement syncStmt, T t);
}
