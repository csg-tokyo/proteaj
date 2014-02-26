package proteaj.tast;

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
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();

    buf.append('{');
    for(Statement stmt : statements) buf.append('\n').append(stmt.toJavassistCode());
    buf.append('\n').append('}');

    return buf.toString();
  }

  private List<Statement> statements;
}

