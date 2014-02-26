package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

public class ReturnStatementParser extends PackratParser<ReturnStatement> {
  /* ReturnStatement
   *  : "return" [ Expression ] ';'
   */
  @Override
  protected ParseResult<ReturnStatement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    if (! enable) return DISABLE;

    // "return"
    ParseResult<String> keyword = KeywordParser.getParser("return").applyRule(reader, env);
    if(keyword.isFail()) return fail(keyword, pos, reader);

    // [ Expression ]
    ParseResult<Expression> val = null;
    if(! returnType.equals(CtClass.voidType)) {
      val = ExpressionParser.getParser(returnType, env).applyRule(reader, env);
      if(val.isFail()) return fail(val, pos, reader);
    }

    // ';'
    ParseResult<String> semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if(semicolon.isFail()) return fail(semicolon, pos, reader);

    if(val != null) return success(new ReturnStatement(val.get()));
    else return success(new ReturnStatement());
  }

  public void init(CtClass returnType) {
    this.returnType = returnType;
    this.enable = true;
  }

  public void disable() {
    this.enable = false;
  }

  public static final ReturnStatementParser parser = new ReturnStatementParser();

  private ReturnStatementParser() {}

  private CtClass returnType;
  private boolean enable;

  private static final Failure<ReturnStatement> DISABLE = new Failure<ReturnStatement>("disable parser", 0, 0);
}
