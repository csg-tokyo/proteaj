package proteaj.ir.tast;

public class Keyword extends TypedAST {
  public Keyword(String str) {
    this.str = str;
  }

  public String getName() {
    return str;
  }

  @Override
  public String toJavassistCode() {
    return str;
  }

  @Override
  public String toString() {
    return str;
  }

  private String str;
}