package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ParenthesizedJavaExpressionParser extends PackratParser {
  /* ParenthesizedJavaExpression
   *  : '(' JavaExpression ')'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // '('
    TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
    if(lparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(lparen.getFailLog());
    }

    // JavaExpression
    TypedAST jexpr = JavaExpressionParser.parser.applyRule(reader, env);
    if(jexpr.isFail()) {
      reader.setPos(pos);
      return new BadAST(jexpr.getFailLog());
    }

    // ')'
    TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
    if(rparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(rparen.getFailLog());
    }

    return jexpr;
  }

  @Override
  public String toString() {
    return "ParenthesizedJavaExpressionParser";
  }

  public static final ParenthesizedJavaExpressionParser parser = new ParenthesizedJavaExpressionParser();

  private ParenthesizedJavaExpressionParser() {}
}

