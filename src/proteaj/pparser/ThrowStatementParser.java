package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ThrowStatementParser extends PackratParser<Statement> {
  /* ThrowStatement
   *  : "throw" Expression ';'
   */
  @Override
  protected ParseResult<Statement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // "throw"
    ParseResult<String> keyword = KeywordParser.getParser("throw").applyRule(reader, env);
    if(keyword.isFail()) return fail(keyword, pos, reader);

    // Expression
    ParseResult<Expression> exception = ExpressionParser.getParser(IRCommonTypes.getThrowableType(), env).applyRule(reader, env);
    if(exception.isFail()) return fail(exception, pos, reader);

    // ';'
    ParseResult<String> semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if(semicolon.isFail()) return fail(semicolon, pos, reader);

    try {
      env.addException(exception.get().getType(), reader.getLine());
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
    }

    return success(new ThrowStatement(exception.get()));
  }

  public static final ThrowStatementParser parser = new ThrowStatementParser();

  private ThrowStatementParser() {}
}

