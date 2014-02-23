package proteaj.pparser;

import proteaj.io.SourceStringReader;
import proteaj.ir.Environment;
import proteaj.ir.tast.*;

import javassist.*;

public class ExpressionStatementParser extends PackratParser<ExpressionStatement> {
  /* ExpressionStatement
   *  : Expression ';'
   */
  @Override
  protected ParseResult<ExpressionStatement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = ExpressionParser.getParser(CtClass.voidType, env).applyRule(reader, env);
    if (expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> semiColon = KeywordParser.getParser(";").applyRule(reader, env);
    if (semiColon.isFail()) return fail(semiColon, pos, reader);

    return success(new ExpressionStatement(expr.get()));
  }

  public static final ExpressionStatementParser parser = new ExpressionStatementParser();

  private ExpressionStatementParser() {}
}

