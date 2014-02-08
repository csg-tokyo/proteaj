package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class AssignExpressionParser extends PackratParser {
  /* AssignExpression
   *  : JavaExpression '=' Expression
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    TypedAST eq = KeywordParser.getParser("=").applyRule(reader, env);
    if(eq.isFail()) {
      reader.setPos(pos);
      return new BadAST(eq.getFailLog());
    }

    TypedAST val = ExpressionParser.getParser(((Expression)expr).getType()).applyRule(reader, env);
    if(val.isFail()) {
      reader.setPos(pos);
      return new BadAST(val.getFailLog());
    }

    return new AssignExpression((Expression)expr, (Expression)val);
  }

  @Override
  public String toString() {
    return "AssignExpressionParser";
  }

  public static final AssignExpressionParser parser = new AssignExpressionParser();

  private AssignExpressionParser() {}
}

