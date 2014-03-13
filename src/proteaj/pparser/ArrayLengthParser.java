package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import static proteaj.pparser.PackratParserCombinators.*;

public class ArrayLengthParser extends PackratParser<ArrayLength> {
  /* ArrayLength
   *  : JavaExpression '.' "length"
   */
  @Override
  protected ParseResult<ArrayLength> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = expr_dot_length.applyRule(reader, env);
    if (expr.isFail()) return fail(expr, pos, reader);

    if (expr.get().getType().isArray()) return success(new ArrayLength(expr.get()));
    else return fail("not array type", pos, reader);
  }

  private static final PackratParser<Expression> expr_dot_length =
      postfix(postfix(ref(new ParserThunk<Expression>() {
    @Override
    public PackratParser<Expression> getParser() { return JavaExpressionParser.parser; }
  }), "."), "length");

  public static final ArrayLengthParser parser = new ArrayLengthParser();

  private ArrayLengthParser() {}
}
