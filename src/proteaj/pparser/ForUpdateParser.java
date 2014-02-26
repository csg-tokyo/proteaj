package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

public class ForUpdateParser extends PackratParser<ExpressionList> {
  /* ForUpdate
   *  : ExpressionList
   */
  @Override
  protected ParseResult<ExpressionList> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<ExpressionList> exprs = ExpressionListParser.parser.applyRule(reader, env);
    if(exprs.isFail()) return fail(exprs, pos, reader);
    else return exprs;
  }

  public static final ForUpdateParser parser = new ForUpdateParser();

  private ForUpdateParser() {}
}

