package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class BlockStatementParser extends PackratParser {
  /* BlockStatement
   *  : Block
   *  | ControlFlow
   *  | LocalVarDeclStatement
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

    // LocalVarDeclStatement
    TypedAST lvdecl = LocalVarDeclStatementParser.parser.applyRule(reader, env, pos);
    if(! lvdecl.isFail()) return lvdecl;

    // ExpressionStatement
    TypedAST expstmt = ExpressionStatementParser.parser.applyRule(reader, env, pos);
    if(! expstmt.isFail()) return expstmt;

    // fail
    FailLog best = chooseBest(block.getFailLog(), cflow.getFailLog(), lvdecl.getFailLog(), expstmt.getFailLog());

    reader.setPos(pos);
    return new BadAST(best);
  }

  public static final BlockStatementParser parser = new BlockStatementParser();

  private BlockStatementParser() {}
}

