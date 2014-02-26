package proteaj.tast;

public class ReadasOperator extends TypedAST {
  public ReadasOperator(String str) {
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
