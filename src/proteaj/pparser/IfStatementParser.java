package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class IfStatementParser extends PackratParser {
  /* IfStatement
   *  : "if" '(' Expression ')' SingleStatement [ "else" SigleStatement ]
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "if"
    TypedAST ifkeyword = KeywordParser.getParser("if").applyRule(reader, env);
    if(ifkeyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(ifkeyword.getFailLog());
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
    TypedAST thenStmt = SingleStatementParser.parser.applyRule(reader, env);
    if(thenStmt.isFail()) {
      reader.setPos(pos);
      return new BadAST(thenStmt.getFailLog());
    }

    int elsepos = reader.getPos();

    // "else"
    TypedAST elsekeyword = KeywordParser.getParser("else").applyRule(reader, env);
    if(elsekeyword.isFail()) {
      reader.setPos(elsepos);
      return new IfStatement((Expression)condition, (Statement)thenStmt);
    }

    // SingleStatement
    TypedAST elseStmt = SingleStatementParser.parser.applyRule(reader, env);
    if(elseStmt.isFail()) {
      reader.setPos(pos);
      return new BadAST(elseStmt.getFailLog());
    }

    return new IfStatement((Expression)condition, (Statement)thenStmt, (Statement)elseStmt);
  }

  public static final IfStatementParser parser = new IfStatementParser();

  private IfStatementParser() {}
}

