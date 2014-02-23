package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ForUpdateParser extends PackratParser<Expression> {
  /* ForUpdate
   *  : ExpressionList
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> exprs = ExpressionListParser.parser.applyRule(reader, env);
    if(exprs.isFail()) return fail(exprs, pos, reader);
    else return exprs;
  }

  public static final ForUpdateParser parser = new ForUpdateParser();

  private ForUpdateParser() {}
}

