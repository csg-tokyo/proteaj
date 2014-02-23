package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class IfStatementParser extends PackratParser<IfStatement> {
  /* IfStatement
   *  : "if" '(' Expression ')' SingleStatement [ "else" SigleStatement ]
   */
  @Override
  protected ParseResult<IfStatement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // "if"
    ParseResult<String> ifKeyword = KeywordParser.getParser("if").applyRule(reader, env);
    if(ifKeyword.isFail()) return fail(ifKeyword, pos, reader);

    // '('
    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if(lPar.isFail()) return fail(lPar, pos, reader);

    // Expression
    ParseResult<Expression> condition = ExpressionParser.getParser(CtClass.booleanType, env).applyRule(reader, env);
    if(condition.isFail()) return fail(condition, pos, reader);

    // ')'
    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
    if(rPar.isFail()) return fail(rPar, pos, reader);

    // SingleStatement
    ParseResult<Statement> thenStmt = SingleStatementParser.parser.applyRule(reader, env);
    if(thenStmt.isFail()) return fail(thenStmt, pos, reader);

    int elsePos = reader.getPos();

    // "else"
    ParseResult<String> elseKeyword = KeywordParser.getParser("else").applyRule(reader, env);
    if(elseKeyword.isFail()) {
      reader.setPos(elsePos);
      return success(new IfStatement(condition.get(), thenStmt.get()));
    }

    // SingleStatement
    ParseResult<Statement> elseStmt = SingleStatementParser.parser.applyRule(reader, env);
    if(elseStmt.isFail()) return fail(elseStmt, pos, reader);

    return success(new IfStatement(condition.get(), thenStmt.get(), elseStmt.get()));
  }

  public static final IfStatementParser parser = new IfStatementParser();

  private IfStatementParser() {}
}

