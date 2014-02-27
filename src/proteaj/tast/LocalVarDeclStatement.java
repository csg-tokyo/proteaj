package proteaj.tast;

import proteaj.tast.util.*;

public class LocalVarDeclStatement extends Statement {
  public LocalVarDeclStatement(LocalVarDecl lvdecl) {
    this.lvdecl = lvdecl;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final LocalVarDecl lvdecl;
}

