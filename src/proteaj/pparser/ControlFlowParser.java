package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class ControlFlowParser extends PackratParser {
  /* ControlFlow
   *  : IfStatement
   *  | WhileStatement
   *  | ForStatement
   *  | ThrowStatement
   *  | TryStatement
   *  | ReturnStatement
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // IfStatement
    TypedAST ifstmt = IfStatementParser.parser.applyRule(reader, env, pos);
    if(! ifstmt.isFail()) return ifstmt;

    // WhileStatement
    TypedAST whilestmt = WhileStatementParser.parser.applyRule(reader, env, pos);
    if(! whilestmt.isFail()) return whilestmt;

    // ForStatement
    TypedAST forstmt = ForStatementParser.parser.applyRule(reader, env, pos);
    if(! forstmt.isFail()) return forstmt;

    // ThrowStatement
    TypedAST throwstmt = ThrowStatementParser.parser.applyRule(reader, env, pos);
    if(! throwstmt.isFail()) return throwstmt;

    // TryStatement
    TypedAST trystmt = TryStatementParser.parser.applyRule(reader, env, pos);
    if(! trystmt.isFail()) return trystmt;

    // ReturnStatement
    TypedAST returnstmt = ReturnStatementParser.parser.applyRule(reader, env, pos);
    if(! returnstmt.isFail()) return returnstmt;

    FailLog best = chooseBest(ifstmt.getFailLog(), whilestmt.getFailLog(), forstmt.getFailLog(), throwstmt.getFailLog(), trystmt.getFailLog(), returnstmt.getFailLog());

    reader.setPos(pos);
    return new BadAST(best);
  }

  public static final ControlFlowParser parser = new ControlFlowParser();

  private ControlFlowParser() {}
}

