package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class SingleStatementParser extends PackratParser {
  /* SingleStatement
   *  : Block
   *  | ControlFlow
   *  | ExpressionStatement
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // Block
    TypedAST block = BlockParser.parser.applyRule(reader, env, pos);
    if(! block.isFail()) return block;

    // ControlFlow
    TypedAST cflow = ControlFlowParser.parser.applyRule(reader, env, pos);
    if(! cflow.isFail()) return cflow;

    // ExpressionStatement
    TypedAST expstmt = ExpressionStatementParser.parser.applyRule(reader, env, pos);
    if(! expstmt.isFail()) return expstmt;

    // fail
    FailLog best = chooseBest(block.getFailLog(), cflow.getFailLog(), expstmt.getFailLog());
    reader.setPos(pos);
    return new BadAST(best);
  }

  public static final SingleStatementParser parser = new SingleStatementParser();

  private SingleStatementParser() {}
}

