package proteaj.pparser;

import proteaj.io.SourceStringReader;
import proteaj.ir.Environment;
import proteaj.ir.tast.*;

public class ArrayLengthParser extends PackratParser<ArrayLength> {
  /* ArrayLength
   *  : JavaExpression '.' "length"
   */
  @Override
  protected ParseResult<ArrayLength> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if (expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);
    if (dot.isFail()) return fail(dot, pos, reader);

    ParseResult<String> len = KeywordParser.getParser("length").applyRule(reader, env);
    if (len.isFail()) return fail(len, pos, reader);

    if (expr.get().getType().isArray()) return success(new ArrayLength(expr.get()));
    else return fail("not array type", pos, reader);
  }

  public static final ArrayLengthParser parser = new ArrayLengthParser();

  private ArrayLengthParser() {}
}

