package proteaj.pparser;

import proteaj.error.*;
import proteaj.ir.tast.*;

public class ArrayLengthParser extends ComposedParser_Sequential {
  /* ArrayLength
   *  : JavaExpression '.' "length"
   */
  private ArrayLengthParser() {
    super("ArrayLengthParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] {
        JavaExpressionParser.parser,
        KeywordParser.getParser("."),
        KeywordParser.getParser("length")
    };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    Expression expr = (Expression)as[0];
    if (expr.getType().isArray()) return new ArrayLength(expr);
    else return new BadAST(new FailLog("not array type", pos, line));
  }

  public static final ArrayLengthParser parser = new ArrayLengthParser();
}

