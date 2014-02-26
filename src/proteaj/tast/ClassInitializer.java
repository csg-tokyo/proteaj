package proteaj.tast;

public class ClassInitializer {
  public ClassInitializer(Block block) {
    this.block = block;
  }

  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  public final Block block;
}

