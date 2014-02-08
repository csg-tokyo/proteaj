package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ArrayLengthParser extends PackratParser {
  /* ArrayLength
   *  : JavaExpression '.' "length"
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // JavaExpression
    TypedAST jexpr = JavaExpressionParser.parser.applyRule(reader, env);
    if(jexpr.isFail()) {
      reader.setPos(pos);
      return new BadAST(jexpr.getFailLog());
    }

    Expression expr = (Expression)jexpr;

    // '.'
    TypedAST dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) {
      reader.setPos(pos);
      return new BadAST(dot.getFailLog());
    }

    // "length"
    if(expr.getType().isArray()) {
      TypedAST length = KeywordParser.getParser("length").applyRule(reader, env);
      if(! length.isFail()) return new ArrayLength(expr);
    }

    FailLog flog = new FailLog("not array length expression", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return "ArrayLengthParser";
  }

  public static final ArrayLengthParser parser = new ArrayLengthParser();

  private ArrayLengthParser() {}
}

