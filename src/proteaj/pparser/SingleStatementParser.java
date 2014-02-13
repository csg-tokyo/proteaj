package proteaj.pparser;

public class SingleStatementParser extends ComposedParser_Alternative {
  /* SingleStatement
   *  : Block
   *  | ControlFlow
   *  | ExpressionStatement
   */
  private SingleStatementParser() {
    super("SingleStatementParser", BlockParser.parser, ControlFlowParser.parser, ExpressionStatementParser.parser);
  }

  public static final SingleStatementParser parser = new SingleStatementParser();
}

