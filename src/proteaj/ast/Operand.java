package proteaj.ast;

public class Operand extends OperatorPatternElement {
  public Operand(String name, int line) {
    super(line);
    this.name = name;
    this.option = OPTION_NONE;
    this.oparg = null;
  }

  @Override
  public boolean isOperator() {
    return false;
  }

  @Override
  public boolean isOperand() {
    return true;
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

  public void setStarOption() {
    this.option = OPTION_STAR;
  }

  public void setPlusOption() {
    this.option = OPTION_PLUS;
  }

  public void setQuestionOption() {
    this.option = OPTION_QUESTION;
  }

  public void setOptionArg(String arg) {
    this.oparg = arg;
  }

  public boolean hasVarArgs() {
    return option == OPTION_STAR || option == OPTION_PLUS;
  }

  public boolean isOption() {
    return option == OPTION_QUESTION;
  }

  public int getOption() {
    return option;
  }

  public boolean hasOptionArg() {
    return oparg != null;
  }

  public String getOptionArg() {
    return oparg;
  }

  private String name;
  private int option;
  private String oparg;

  public static final int OPTION_NONE = 0;
  public static final int OPTION_STAR = 1;
  public static final int OPTION_PLUS = 2;
  public static final int OPTION_QUESTION = 3;
}

