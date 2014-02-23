package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class AssignExpressionParser extends PackratParser<Expression> {
  /* AssignExpression
   *  : JavaExpression '=' Expression
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> eq = KeywordParser.getParser("=").applyRule(reader, env);
    if(eq.isFail()) return fail(eq, pos, reader);

    ParseResult<Expression> val = ExpressionParser.getParser(expr.get().getType(), env).applyRule(reader, env);
    if(val.isFail()) return fail(val, pos, reader);

    return success(new AssignExpression(expr.get(), val.get()));
  }

  @Override
  public String toString() {
    return "AssignExpressionParser";
  }

  public static final AssignExpressionParser parser = new AssignExpressionParser();

  private AssignExpressionParser() {}
}

