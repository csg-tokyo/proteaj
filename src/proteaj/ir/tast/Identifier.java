package proteaj.ir.tast;

public class Identifier extends TypedAST {
  public Identifier(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toJavassistCode() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}
