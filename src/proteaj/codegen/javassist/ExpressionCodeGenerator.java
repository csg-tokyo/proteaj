package proteaj.codegen.javassist;

import proteaj.tast.*;

public class ExpressionCodeGenerator {
  public static final ExpressionCodeGenerator instance = new ExpressionCodeGenerator();

  public StringBuilder visit (Expression expr, StringBuilder buf) {
    return buf.append(expr.toJavassistCode());
  }

  private ExpressionCodeGenerator() {}
}
