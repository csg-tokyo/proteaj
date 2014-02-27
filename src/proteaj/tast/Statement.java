package proteaj.tast;

import proteaj.tast.util.*;

public abstract class Statement {
  public abstract <T> T accept(StatementVisitor<T> visitor, T t);
}
