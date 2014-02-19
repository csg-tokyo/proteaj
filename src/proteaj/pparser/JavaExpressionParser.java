package proteaj.pparser;

public class JavaExpressionParser extends ComposedParser_Alternative {
  /* JavaExpression
   *  : AssignExpression
   *  | ArrayLength
   *  | MethodCall
   *  | FieldAccess
   *  | ArrayAccess
   *  | Primary
   */
  private JavaExpressionParser() {
    super("JavaExpressionParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] {
        AssignExpressionParser.parser,
        ArrayLengthParser.parser,
        MethodCallParser.parser,
        FieldAccessParser.parser,
        ArrayAccessParser.parser,
        PrimaryParser.parser
    };
  }

  public static final JavaExpressionParser parser = new JavaExpressionParser();
}

