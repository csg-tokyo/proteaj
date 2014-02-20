package proteaj.pparser;

import proteaj.ir.Environment;

public class ControlFlowParser extends ComposedParser_Alternative {
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
  protected PackratParser[] getParsers(Environment env) {
    return new PackratParser[] {
        IfStatementParser.parser,
        WhileStatementParser.parser,
        ForStatementParser.parser,
        ThrowStatementParser.parser,
        TryStatementParser.parser,
        ReturnStatementParser.parser
    };
  }

  public static final ControlFlowParser parser = new ControlFlowParser();
}

