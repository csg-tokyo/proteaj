package proteaj.codegen.javassist;

import proteaj.tast.*;

import java.util.List;

public class ArgumentsCodeGenerator {
  public static final ArgumentsCodeGenerator instance = new ArgumentsCodeGenerator();

  public StringBuilder visit (Arguments arguments, StringBuilder buf) {
    List<Expression> args = arguments.getArgs();

    if(args.isEmpty()) return buf.append("()");

    buf.append("(");
    buf = ExpressionCodeGenerator.instance.visit(args.get(0), buf);

    for(int i = 1; i < args.size(); i++) {
      buf = ExpressionCodeGenerator.instance.visit(args.get(i), buf.append(','));
    }

    return buf.append(')');
  }

  private ArgumentsCodeGenerator() {}
}
