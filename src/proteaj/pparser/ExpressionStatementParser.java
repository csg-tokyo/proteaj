package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ExpressionStatementParser extends PackratParser {
  /* ExpressionStatement
   *  : Expression ';'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = ExpressionParser.getParser(CtClass.voidType).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    TypedAST semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if(semicolon.isFail()) {
      reader.setPos(pos);
      return new BadAST(semicolon.getFailLog());
    }

    return new ExpressionStatement((Expression)expr);
  }

  public static final ExpressionStatementParser parser = new ExpressionStatementParser();

  private ExpressionStatementParser() {}
}

