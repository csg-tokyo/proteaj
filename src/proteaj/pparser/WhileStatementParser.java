package proteaj.pparser;

import proteaj.io.SourceStringReader;
import proteaj.ir.Environment;
import proteaj.ir.tast.*;

import javassist.*;

public class WhileStatementParser extends PackratParser<Statement> {
  /* WhileStatement
   *  : "while" '(' Expression ')' SingleStatement
   */
  @Override
  protected ParseResult<Statement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // "while"
    ParseResult<String> whileKeyword = KeywordParser.getParser("while").applyRule(reader, env);
    if (whileKeyword.isFail()) return fail(whileKeyword, pos, reader);

    // '('
    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if (lPar.isFail()) return fail(lPar, pos, reader);

    // Expression
    ParseResult<Expression> condition = ExpressionParser.getParser(CtClass.booleanType, env).applyRule(reader, env);
    if (condition.isFail()) return fail(condition, pos, reader);

    // ')'
    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
    if (rPar.isFail()) return fail(rPar, pos, reader);

    // SingleStatement
    ParseResult<Statement> stmt = SingleStatementParser.parser.applyRule(reader, env);
    if (stmt.isFail()) return fail(stmt, pos, reader);

    return success(new WhileStatement(condition.get(), stmt.get()));
  }

  public static final WhileStatementParser parser = new WhileStatementParser();

  private WhileStatementParser() {}
}

