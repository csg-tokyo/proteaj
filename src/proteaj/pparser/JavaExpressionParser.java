package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.tast.Expression;

import java.util.*;

public class JavaExpressionParser extends ComposedParser_Alternative<Expression> {
  /* JavaExpression
   *  : AssignExpression
   *  | ArrayLength
   *  | MethodCall
   *  | FieldAccess
   *  | ArrayAccess
   *  | Primary
   */
  private JavaExpressionParser() {
    super("JavaExpressionParser");
  }

  @Override
  protected List<PackratParser<? extends  Expression>> getParsers(Environment env) {
    return asList(
        AssignExpressionParser.parser,
        ArrayLengthParser.parser,
        MethodCallParser.parser,
        FieldAccessParser.parser,
        ArrayAccessParser.parser,
        PrimaryParser.parser
    );
  }

  public static final JavaExpressionParser parser = new JavaExpressionParser();
}

