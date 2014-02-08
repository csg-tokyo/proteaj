package proteaj.ast;

public abstract class AST {
  public AST(int line) {
    this.line = line;
  }

  public AST(AST ast) {
    this.line = ast.line;
  }

  public int getLine() {
    return line;
  }

  private int line;
}

