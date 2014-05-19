package proteaj.tast;

import java.util.List;

public class CaseBlock {
  // default case
  public CaseBlock(List<Statement> stmts) {
    this.label = null;
    this.stmts = stmts;
  }

  public CaseBlock(Expression label, List<Statement> stmts) {
    this.label = label;
    this.stmts = stmts;
  }

  public boolean isDefault () {
    return label == null;
  }

  public Expression getLabel () {
    assert ! isDefault();
    return label;
  }

  public static final int LABEL = 100;

  public final List<Statement> stmts;

  private final Expression label;

}
