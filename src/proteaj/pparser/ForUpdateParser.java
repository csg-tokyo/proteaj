package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ForUpdateParser extends PackratParser {
  /* ForUpdate
   *  : ExpressionList
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST exprs = ExpressionListParser.parser.applyRule(reader, env);
    if(exprs.isFail()) {
      reader.setPos(pos);
      return new BadAST(exprs.getFailLog());
    }

    return exprs;
  }

  public static final ForUpdateParser parser = new ForUpdateParser();

  private ForUpdateParser() {}
}

