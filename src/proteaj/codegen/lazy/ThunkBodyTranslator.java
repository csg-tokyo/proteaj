package proteaj.codegen.lazy;

import proteaj.tast.*;
import proteaj.tast.util.*;

import java.util.*;

public class ThunkBodyTranslator extends TreeTranslator {
  public ThunkBodyTranslator(Map<Expression, FieldAccess> map) {
    this.map = map;
  }

  @Override
  public Expression translate(Expression expression) {
    if (map.containsKey(expression)) return map.get(expression);
    return super.translate(expression);
  }

  private final Map<Expression, FieldAccess> map;
}
