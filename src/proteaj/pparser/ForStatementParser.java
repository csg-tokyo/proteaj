package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

public class ForStatementParser extends PackratParser<Statement> {
  /* ForStatement
   *  : "for" '(' ForInit ';' ForCond ';' ForUpdate ')' SingleStatement
   */
  @Override
  protected ParseResult<Statement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> keyword = KeywordParser.getParser("for").applyRule(reader, env);
    if(keyword.isFail()) return fail(keyword, pos, reader);

    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if(lPar.isFail()) return fail(lPar, pos, reader);

    Environment newenv = new Environment(env);

    ParseResult<Expression> init = ForInitParser.parser.applyRule(reader, newenv);
    if(init.isFail()) return fail(init, pos, reader);

    ParseResult<String> semicolon1 = KeywordParser.getParser(";").applyRule(reader, newenv);
    if(semicolon1.isFail()) return fail(semicolon1, pos, reader);

    ParseResult<Expression> cond = ForCondParser.parser.applyRule(reader, newenv);
    if(cond.isFail()) return fail(cond, pos, reader);

    ParseResult<String> semicolon2 = KeywordParser.getParser(";").applyRule(reader, newenv);
    if(semicolon2.isFail()) return fail(semicolon2, pos, reader);

    ParseResult<ExpressionList> update = ForUpdateParser.parser.applyRule(reader, newenv);
    if(update.isFail()) return fail(update, pos, reader);

    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, newenv);
    if(rPar.isFail()) return fail(rPar, pos, reader);

    ParseResult<Statement> stmt = SingleStatementParser.parser.applyRule(reader, newenv);
    if(stmt.isFail()) return fail(stmt, pos, reader);

    env.inheritExceptions(newenv);

    return success(new ForStatement(init.get(), cond.get(), update.get(), stmt.get()));
  }

  public static final ForStatementParser parser = new ForStatementParser();

  private ForStatementParser() {}
}

