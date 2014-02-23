package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.ir.tast.Statement;

import java.util.*;

public class SingleStatementParser extends ComposedParser_Alternative<Statement> {
  /* SingleStatement
   *  : Block
   *  | ControlFlow
   *  | ExpressionStatement
   */
  private SingleStatementParser() {
    super("SingleStatementParser");
  }

  @Override
  protected List<PackratParser<? extends  Statement>> getParsers(Environment env) {
    return asList( BlockParser.parser, ControlFlowParser.parser, ExpressionStatementParser.parser );
  }

  public static final SingleStatementParser parser = new SingleStatementParser();
}

