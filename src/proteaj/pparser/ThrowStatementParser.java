package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ThrowStatementParser extends PackratParser {
  /* ThrowStatement
   *  : "throw" Expression ';'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "throw"
    TypedAST keyword = KeywordParser.getParser("throw").applyRule(reader, env);
    if(keyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(keyword.getFailLog());
    }

    int line = reader.getLine();

    // Expression
    TypedAST exception = ExpressionParser.getParser(IRCommonTypes.getThrowableType(), env).applyRule(reader, env);
    if(exception.isFail()) {
      reader.setPos(pos);
      return new BadAST(exception.getFailLog());
    }

    // ';'
    TypedAST semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if(semicolon.isFail()) {
      reader.setPos(pos);
      return new BadAST(semicolon.getFailLog());
    }

    Expression expr = (Expression)exception;

    try {
      env.addException(expr.getType(), line);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), line));
    }

    return new ThrowStatement(expr);
  }

  public static final ThrowStatementParser parser = new ThrowStatementParser();

  private ThrowStatementParser() {}
}

