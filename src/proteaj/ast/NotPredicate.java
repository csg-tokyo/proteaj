package proteaj.ast;

public class NotPredicate extends OperatorPatternElement {
  public NotPredicate(String cls, int line) {
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
    return false;
  }

  @Override
  public boolean isNotPredicate() {
    return true;
  }

  @Override
  public String getName() {
    return cls;
  }

  private String cls;
}