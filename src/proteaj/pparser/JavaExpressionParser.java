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
    super("JavaExpressionParser",
        AssignExpressionParser.parser,
        ArrayLengthParser.parser,
        MethodCallParser.parser,
        FieldAccessParser.parser,
        ArrayAccessParser.parser,
        PrimaryParser.parser
    );
  }

  public static final JavaExpressionParser parser = new JavaExpressionParser();
}

