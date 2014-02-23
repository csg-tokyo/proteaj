package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.ir.tast.Statement;

import java.util.*;

public class ControlFlowParser extends ComposedParser_Alternative<Statement> {
  /* ControlFlow
   *  : IfStatement
   *  | WhileStatement
   *  | ForStatement
   *  | ThrowStatement
   *  | TryStatement
   *  | ReturnStatement
   */
  private ControlFlowParser() {
    super("ControlFlowParser");
  }

  @Override
  protected List<PackratParser<? extends  Statement>> getParsers(Environment env) {
    return asList(
        IfStatementParser.parser,
        WhileStatementParser.parser,
        ForStatementParser.parser,
        ThrowStatementParser.parser,
        TryStatementParser.parser,
        ReturnStatementParser.parser
    );
  }

  public static final ControlFlowParser parser = new ControlFlowParser();
}

