package proteaj.pparser;

import proteaj.io.SourceStringReader;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ParenthesizedJavaExpressionParser extends PackratParser<Expression> {
  /* ParenthesizedJavaExpression
   *  : '(' JavaExpression ')'
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if(lPar.isFail()) return fail(lPar, pos, reader);

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
    if(rPar.isFail()) return fail(rPar, pos, reader);

    return expr;
  }

  public static final ParenthesizedJavaExpressionParser parser = new ParenthesizedJavaExpressionParser();

  private ParenthesizedJavaExpressionParser() {}
}

