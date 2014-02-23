package proteaj.ir.tast;

import java.util.*;

public class QualifiedIdentifier extends TypedAST {
  public QualifiedIdentifier(Identifier id) {
    qid = new ArrayList<Identifier>();
    qid.add(id);
  }

  public void append(Identifier id) {
    qid.add(id);
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(qid.get(0).getName());

    int size = qid.size();
    for(int i = 1; i < size; i++) {
      buf.append('.').append(qid.get(i).getName());
    }

    return buf.toString();
  }

  @Override
  public String toJavassistCode() {
    return toString();
  }

  private List<Identifier> qid;
}