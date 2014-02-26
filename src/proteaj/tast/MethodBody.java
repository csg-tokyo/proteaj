package proteaj.tast;

public class MethodBody {
  public MethodBody(Block block) {
    this.block = block;
  }

  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  public final Block block;
}

