package proteaj.tast;

public class ConstructorBody extends TypedAST {
  public ConstructorBody(Block block) {
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

