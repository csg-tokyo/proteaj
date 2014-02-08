package proteaj.ast;

public class AndPredicate extends OperatorPatternElement {
  public AndPredicate(String cls, int line) {
    super(line);
    this.cls = cls;
  }

  @Override
  public boolean isOperator() {
    return false;
  }

  @Override
  public boolean isOperand() {
    return false;
  }

  @Override
  public boolean isAndPredicate() {
    return true;
  }

  @Override
  public boolean isNotPredicate() {
    return false;
  }

  @Override
  public String getName() {
    return cls;
  }

  private String cls;
}
