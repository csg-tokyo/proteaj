package proteaj.ast;

public abstract class OperatorPatternElement extends AST {
  public OperatorPatternElement(int line) {
    super(line);
  }

  public abstract boolean isOperator();
  public abstract boolean isOperand();

  public abstract boolean isAndPredicate();
  public abstract boolean isNotPredicate();

  public abstract String getName();
}

