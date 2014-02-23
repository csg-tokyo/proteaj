package proteaj.ir.tast;

public class ClassInitializer extends TypedAST {
  public ClassInitializer(Statement block) {
    this.block = block;
  }

  @Override
  public String toJavassistCode() {
    return block.toJavassistCode();
  }

  private Statement block;
}

