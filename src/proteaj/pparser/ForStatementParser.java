package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ForStatementParser extends PackratParser {
  /* ForStatement
   *  : "for" '(' ForInit ';' ForCond ';' ForUpdate ')' SingleStatement
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST keyword = KeywordParser.getParser("for").applyRule(reader, env);
    if(keyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(keyword.getFailLog());
    }

    TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
    if(lparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(lparen.getFailLog());
    }

    Environment newenv = new Environment(env);

    TypedAST init = ForInitParser.parser.applyRule(reader, newenv);
    if(init.isFail()) {
      reader.setPos(pos);
      return new BadAST(init.getFailLog());
    }

    TypedAST semicolon1 = KeywordParser.getParser(";").applyRule(reader, newenv);
    if(semicolon1.isFail()) {
      reader.setPos(pos);
      return new BadAST(semicolon1.getFailLog());
    }

    TypedAST cond = ForCondParser.parser.applyRule(reader, newenv);
    if(cond.isFail()) {
      reader.setPos(pos);
      return new BadAST(cond.getFailLog());
    }

    TypedAST semicolon2 = KeywordParser.getParser(";").applyRule(reader, newenv);
    if(semicolon2.isFail()) {
      reader.setPos(pos);
      return new BadAST(semicolon2.getFailLog());
    }

    TypedAST update = ForUpdateParser.parser.applyRule(reader, newenv);
    if(update.isFail()) {
      reader.setPos(pos);
      return new BadAST(update.getFailLog());
    }

    TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, newenv);
    if(rparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(rparen.getFailLog());
    }

    TypedAST stmt = SingleStatementParser.parser.applyRule(reader, newenv);
    if(stmt.isFail()) {
      reader.setPos(pos);
      return new BadAST(stmt.getFailLog());
    }

    env.inheritExceptions(newenv);

    return new ForStatement((Expression)init, (Expression)cond, (Expression)update, (Statement)stmt);
  }

  public static final ForStatementParser parser = new ForStatementParser();

  private ForStatementParser() {}
}

