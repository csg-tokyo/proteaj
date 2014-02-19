package proteaj.pparser;

import proteaj.ir.tast.*;

public class ParenthesizedJavaExpressionParser extends ComposedParser_Sequential {
  /* ParenthesizedJavaExpression
   *  : '(' JavaExpression ')'
   */
  private ParenthesizedJavaExpressionParser() {
    super("ParenthesizedJavaExpressionParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] { KeywordParser.getParser("("), JavaExpressionParser.parser, KeywordParser.getParser(")") };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    return as[1];
  }

  public static final ParenthesizedJavaExpressionParser parser = new ParenthesizedJavaExpressionParser();


}

