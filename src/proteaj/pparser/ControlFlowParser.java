package proteaj.pparser;

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
    super("ControlFlowParser",
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

