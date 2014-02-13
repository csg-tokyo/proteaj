package proteaj.pparser;

public class BlockStatementParser extends ComposedParser_Alternative {
  /* BlockStatement
   *  : Block
   *  | ControlFlow
   *  | LocalVarDeclStatement
   *  | ExpressionStatement
   */
  private BlockStatementParser() {
    super("BlockStatementParser",
        BlockParser.parser,
        ControlFlowParser.parser,
        LocalVarDeclStatementParser.parser,
        ExpressionStatementParser.parser
    );
  }

  public static final BlockStatementParser parser = new BlockStatementParser();
}

