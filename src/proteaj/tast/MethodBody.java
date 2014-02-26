package proteaj.tast;

public class MethodBody extends TypedAST {
  public MethodBody(Block block) {
    this.block = block;
  }

  @Override
  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  private Block block;
}

