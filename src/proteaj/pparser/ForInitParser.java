package proteaj.pparser;

import proteaj.ir.Environment;

public class ForInitParser extends ComposedParser_Alternative {
  /* ForInit
   *  : ExpressionList
   *  | LocalVarDecl
   */
  private ForInitParser() {
    super("ForInitParser");
  }

  @Override
  protected PackratParser[] getParsers(Environment env) {
    return new PackratParser[] { LocalVarDeclParser.parser, ExpressionListParser.parser };
  }

  public static final ForInitParser parser = new ForInitParser();
}
