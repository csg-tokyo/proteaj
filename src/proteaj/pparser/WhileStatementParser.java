package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class WhileStatementParser extends PackratParser {
  /* WhileStatement
   *  : "while" '(' Expression ')' SingleStatement
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "while"
    TypedAST keyword = KeywordParser.getParser("while").applyRule(reader, env);
    if(keyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(keyword.getFailLog());
    }

    // '('
    TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
    if(lparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(lparen.getFailLog());
    }

    // Expression
    TypedAST condition = ExpressionParser.getParser(CtClass.booleanType).applyRule(reader, env);
    if(condition.isFail()) {
      reader.setPos(pos);
      return new BadAST(condition.getFailLog());
    }

    // ')'
    TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
    if(rparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(rparen.getFailLog());
    }

    // SingleStatement
    TypedAST stmt = SingleStatementParser.parser.applyRule(reader, env);
    if(stmt.isFail()) {
      reader.setPos(pos);
      return new BadAST(stmt.getFailLog());
    }

    return new WhileStatement((Expression)condition, (Statement)stmt);
  }

  public static final WhileStatementParser parser = new WhileStatementParser();

  private WhileStatementParser() {}
}

