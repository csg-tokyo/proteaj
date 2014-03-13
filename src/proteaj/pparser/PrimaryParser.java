package proteaj.pparser;

import proteaj.tast.Expression;

import static proteaj.pparser.PackratParserCombinators.*;

public class PrimaryParser {
  /* Primary
   *  : AbbMethodCall
   *  | Variable
   *  | StaticMethodCall
   *  | StaticFieldAccess
   *  | NewExpression
   *  | NewArrayExpression
   *  | CastExpression
   *  | Literal
   *  | ParenthesizedJavaExpression
   */
  public static final PackratParser<Expression> parser =
      choice(AbbMethodCallParser.parser, VariableParser.parser, StaticMethodCallParser.parser,
          StaticFieldAccessParser.parser, NewExpressionParser.parser, NewArrayExpressionParser.parser,
          CastExpressionParser.parser, ParenthesizedJavaExpressionParser.parser, LiteralParser.parser);
}

