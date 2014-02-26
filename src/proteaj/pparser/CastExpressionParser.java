package proteaj.pparser;

import javassist.CtClass;
import proteaj.io.SourceStringReader;
import proteaj.ir.*;
import proteaj.tast.*;

import static proteaj.util.CtClassUtil.*;

public class CastExpressionParser extends PackratParser<CastExpression> {
  /* CastExpression
   *  : '(' TypeName ')' JavaExpression
   */
  @Override
  protected ParseResult<CastExpression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> lBrace = KeywordParser.getParser("(").applyRule(reader, env);
    if (lBrace.isFail()) return fail(lBrace, pos, reader);

    ParseResult<CtClass> type = TypeNameParser.parser.applyRule(reader, env);
    if (type.isFail()) return fail(type, pos, reader);

    ParseResult<String> rBrace = KeywordParser.getParser(")").applyRule(reader, env);
    if (rBrace.isFail()) return fail(rBrace, pos, reader);

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if (expr.isFail()) return fail(expr, pos, reader);

    Expression e = expr.get();
    CtClass to = type.get();

    if (isCastable(e.getType(), to, reader.filePath, reader.getLine())) return success(new CastExpression(to, e));
    else return fail(e.getType().getName() + " cannot cast to " + to.getName(), pos, reader);
  }

  public static final CastExpressionParser parser = new CastExpressionParser();

  private CastExpressionParser() {}
}
