package proteaj.pparser;

public class BlockStatementParser extends ComposedParser_Alternative {
  /* BlockStatement
   *  : Block
   *  | ControlFlow
   *  | LocalVarDeclStatement
   *  | ExpressionStatement
   */
  private BlockStatementParser() {
    super("BlockStatementParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] {
        BlockParser.parser,
        ControlFlowParser.parser,
        LocalVarDeclStatementParser.parser,
        ExpressionStatementParser.parser
    };
  }

  public static final BlockStatementParser parser = new BlockStatementParser();
}

