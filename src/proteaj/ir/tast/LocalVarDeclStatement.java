package proteaj.ir.tast;

public class LocalVarDeclStatement extends Statement {
  public LocalVarDeclStatement(LocalVarDecl lvdecl) {
    this.lvdecl = lvdecl;
  }

  @Override
  public String toJavassistCode() {
    return lvdecl.toJavassistCode() + ';';
  }

  private LocalVarDecl lvdecl;
}

