package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.CtClass;

public class DoWhileStatementParser extends PackratParser<DoWhileStatement> {
  /* DoWhileStatement
   *  : "do" SingleStatement "while" '(' Expression ')' ';'
   */
  @Override
  protected ParseResult<DoWhileStatement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // "do"
    ParseResult<String> doKeyword = KeywordParser.getParser("do").applyRule(reader, env);
    if (doKeyword.isFail()) return fail(doKeyword, pos, reader);

    // SingleStatement
    ParseResult<Statement> stmt = SingleStatementParser.parser.applyRule(reader, env);
    if (stmt.isFail()) return fail(stmt, pos, reader);

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

    // ';'
    ParseResult<String> semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if (semicolon.isFail()) return fail(semicolon, pos, reader);

    return success(new DoWhileStatement(stmt.get(), condition.get()));
  }

  public static final DoWhileStatementParser parser = new DoWhileStatementParser();

  private DoWhileStatementParser() {}
}
