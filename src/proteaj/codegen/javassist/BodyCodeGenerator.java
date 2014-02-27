package proteaj.codegen.javassist;

import proteaj.tast.*;

public class BodyCodeGenerator {
  public static String codeGen (MethodBody body)       { return instance.visit(body, new StringBuilder()).toString(); }
  public static String codeGen (ConstructorBody body)  { return instance.visit(body, new StringBuilder()).toString(); }
  public static String codeGen (FieldBody body)        { return instance.visit(body, new StringBuilder()).toString(); }
  public static String codeGen (DefaultValue body)     { return instance.visit(body, new StringBuilder()).toString(); }
  public static String codeGen (ClassInitializer body) { return instance.visit(body, new StringBuilder()).toString(); }

  public static final BodyCodeGenerator instance = new BodyCodeGenerator();

  public StringBuilder visit (MethodBody body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  public StringBuilder visit (ConstructorBody body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  public StringBuilder visit (FieldBody body, StringBuilder buf) {
    return visit(body.expr, buf);
  }

  public StringBuilder visit (DefaultValue body, StringBuilder buf) {
    buf = buf.append('{').append('\n').append("return ");
    buf = visit(body.expr, buf);
    buf = buf.append(';').append('\n').append('}');
    return buf;
  }

  public StringBuilder visit (ClassInitializer body, StringBuilder buf) {
    return visit(body.block, buf);
  }

  private StringBuilder visit (Block block, StringBuilder buf) {
    return StatementCodeGenerator.instance.visit(block, buf);
  }

  private StringBuilder visit (Expression expr, StringBuilder buf) {
    return ExpressionCodeGenerator.instance.visit(expr, buf);
  }

  private BodyCodeGenerator() {}
}
