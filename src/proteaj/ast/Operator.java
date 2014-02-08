package proteaj.ast;

public class Operator extends OperatorPatternElement {
  public Operator(String name, int line) {
    super(line);
    this.name = name;
  }

  @Override
  public boolean isOperator() {
    return true;
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
    return false;
  }

  @Override
  public String getName() {
    return name;
  }

  private String name;
}

