package proteaj.pparser;

public class ForInitParser extends ComposedParser_Alternative {
  /* ForInit
   *  : ExpressionList
   *  | LocalVarDecl
   */
  private ForInitParser() {
    super("ForInitParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] { LocalVarDeclParser.parser, ExpressionListParser.parser };
  }

  public static final ForInitParser parser = new ForInitParser();
}
