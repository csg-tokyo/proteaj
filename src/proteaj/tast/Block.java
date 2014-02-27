package proteaj.tast;

import proteaj.tast.util.StatementVisitor;

import java.util.*;

public class Block extends Statement {
  public Block() {
    this.statements = new ArrayList<Statement>();
  }

  public void addStatement(Statement stmt) {
    statements.add(stmt);
  }

  public List<Statement> getStatements() {
    return statements;
  }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  private List<Statement> statements;
}

