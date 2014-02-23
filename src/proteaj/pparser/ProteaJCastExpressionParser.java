package proteaj.pparser;

import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class ProteaJCastExpressionParser extends ComposedParser_Alternative<CastExpression> {
  /* ProteaJCastExpression
   *  : ProteaJRightArrowCast
   *  | ProteaJLeftArrowCast
   */

  @Override
  protected List<PackratParser<? extends CastExpression>> getParsers(Environment env) {
    return asList(ProteaJRightArrowCastParser.getParser(type), ProteaJLeftArrowCastParser.getParser(type));
  }

  public static ProteaJCastExpressionParser getParser(CtClass type) {
    if (! parsers.containsKey(type)) parsers.put(type, new ProteaJCastExpressionParser(type));
    return parsers.get(type);
  }

  private static Map<CtClass, ProteaJCastExpressionParser> parsers = new HashMap<CtClass, ProteaJCastExpressionParser>();

  private ProteaJCastExpressionParser(CtClass type) {
    super("ProteaJCastExpressionParser");
    this.type = type;
  }

  private final CtClass type;
}
