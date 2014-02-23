package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.ir.tast.Statement;

import java.util.*;

public class BlockStatementParser extends ComposedParser_Alternative<Statement> {
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
  protected List<PackratParser<? extends  Statement>> getParsers(Environment env) {
    return asList(BlockParser.parser, ControlFlowParser.parser,
        LocalVarDeclStatementParser.parser, ExpressionStatementParser.parser);
  }

  public static final BlockStatementParser parser = new BlockStatementParser();
}

