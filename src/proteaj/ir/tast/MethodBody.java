package proteaj.ir.tast;

public class MethodBody extends TypedAST {
  public MethodBody(Block block) {
    this.block = block;
  }

  public Block getBlock() {
    return block;
  }

  @Override
  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  private Block block;
}

