package proteaj.ir.tast;

public class MethodBody extends TypedAST {
  public MethodBody(Statement block) {
    this.block = block;
  }

  @Override
  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  private Statement block;
}

