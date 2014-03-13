package proteaj.pparser;

import proteaj.tast.*;

import static proteaj.pparser.PackratParserCombinators.*;

public class JavaExpressionParser {
  /* JavaExpression
   *  : AssignExpression
   *  | ArrayLength
   *  | MethodCall
   *  | FieldAccess
   *  | ArrayAccess
   *  | Primary
   */
  public static final PackratParser<Expression> parser =
      choice(AssignExpressionParser.parser, ArrayLengthParser.parser, MethodCallParser.parser,
          FieldAccessParser.parser, ArrayAccessParser.parser, PrimaryParser.parser);
}
