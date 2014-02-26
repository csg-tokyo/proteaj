package proteaj.tast;

public class ConstructorBody {
  public ConstructorBody(Block block) {
    this.block = block;
  }

  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  public final Block block;
}

