package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ForInitParser extends PackratParser {
  /* ForInit
   *  : ExpressionList
   *  | LocalVarDecl
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lvdecl = LocalVarDeclParser.parser.applyRule(reader, env, pos);
    if(! lvdecl.isFail()) return lvdecl;

    TypedAST exprs = ExpressionListParser.parser.applyRule(reader, env, pos);
    if(! exprs.isFail()) return exprs;

    FailLog flog = chooseBest(exprs.getFailLog(), lvdecl.getFailLog());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public static final ForInitParser parser = new ForInitParser();

  private ForInitParser() {}
}

