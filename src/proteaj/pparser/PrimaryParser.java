package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.tast.Expression;

import java.util.*;

public class PrimaryParser extends ComposedParser_Alternative<Expression> {
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
  protected List<PackratParser<? extends  Expression>> getParsers(Environment env) {
    return asList(
        AbbMethodCallParser.parser,
        VariableParser.parser,
        StaticMethodCallParser.parser,
        StaticFieldAccessParser.parser,
        NewExpressionParser.parser,
        NewArrayExpressionParser.parser,
        CastExpressionParser.parser,
        ParenthesizedJavaExpressionParser.parser,
        LiteralParser.parser
    );
  }

  public static final PrimaryParser parser = new PrimaryParser();
}
