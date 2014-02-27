package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.tast.Statement;

import java.util.*;

public class ControlFlowParser extends ComposedParser_Alternative<Statement> {
  /* ControlFlow
   *  : IfStatement
   *  | WhileStatement
   *  | DoWhileStatement
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
        DoWhileStatementParser.parser,
        ForStatementParser.parser,
        ThrowStatementParser.parser,
        TryStatementParser.parser,
        ReturnStatementParser.parser
    );
  }

  public static final ControlFlowParser parser = new ControlFlowParser();
}

