package proteaj.pparser;

import proteaj.ir.Environment;

public class PrimaryParser extends ComposedParser_Alternative {
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
  private PrimaryParser() {
    super("PrimaryParser");
  }

  @Override
  protected PackratParser[] getParsers(Environment env) {
    return new PackratParser[] {
        AbbMethodCallParser.parser,
        VariableParser.parser,
        StaticMethodCallParser.parser,
        StaticFieldAccessParser.parser,
        NewExpressionParser.parser,
        NewArrayExpressionParser.parser,
        CastExpressionParser.parser,
        ParenthesizedJavaExpressionParser.parser,
        LiteralParser.parser
    };
  }

  public static final PrimaryParser parser = new PrimaryParser();
}
