package proteaj.tast;

import proteaj.tast.util.*;

public class LocalsDeclStatement extends Statement {
  public LocalsDeclStatement(LocalsDecl localsDecl) {
    this.localsDecl = localsDecl;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final LocalsDecl localsDecl;
}

