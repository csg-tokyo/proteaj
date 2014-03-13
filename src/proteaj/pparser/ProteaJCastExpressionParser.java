package proteaj.pparser;

import proteaj.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.pparser.PackratParserCombinators.*;

public class ProteaJCastExpressionParser {
  /* ProteaJCastExpression
   *  : ProteaJRightArrowCast | ProteaJLeftArrowCast
   */
  public static PackratParser<CastExpression> getParser(CtClass type) {
    if (! parsers.containsKey(type)) parsers.put(type, makeParser(type));
    return parsers.get(type);
  }

  private static PackratParser<CastExpression> makeParser(CtClass type) {
    return choice(ProteaJRightArrowCastParser.getParser(type), ProteaJLeftArrowCastParser.getParser(type));
  }

  private static Map<CtClass, PackratParser<CastExpression>> parsers = new HashMap<CtClass, PackratParser<CastExpression>>();
}