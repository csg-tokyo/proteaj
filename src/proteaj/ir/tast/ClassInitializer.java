package proteaj.ir.tast;

public class ClassInitializer extends TypedAST {
  public ClassInitializer(Block block) {
    this.block = block;
  }

  @Override
  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  private Block block;
}

