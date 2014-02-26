package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.tast.Expression;

import java.util.*;

public class ForInitParser extends ComposedParser_Alternative<Expression> {
  /* ForInit
   *  : ExpressionList
   *  | LocalVarDecl
   */
  private ForInitParser() {
    super("ForInitParser");
  }

  @Override
  protected List<PackratParser<? extends Expression>> getParsers(Environment env) {
    return asList( LocalVarDeclParser.parser, ExpressionListParser.parser );
  }

  public static final ForInitParser parser = new ForInitParser();
}
