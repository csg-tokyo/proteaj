package proteaj.codegen.javassist;

import proteaj.tast.*;

import java.util.List;

public class ArgumentsCodeGenerator {
  public static final ArgumentsCodeGenerator instance = new ArgumentsCodeGenerator();

  public StringBuilder visit (Arguments arguments, StringBuilder buf) {
    if (arguments instanceof VariableArguments) return visit((VariableArguments)arguments, buf);

    List<Expression> args = arguments.getArgs();

    if(args.isEmpty()) return buf.append("()");

    buf = buf.append("(");
    buf = visit(args.get(0), buf);

    for (int i = 1; i < args.size(); i++) {
      buf = visit(args.get(i), buf.append(','));
    }

    return buf.append(')');
  }

  public StringBuilder visit (VariableArguments arguments, StringBuilder buf) {
    final int nargs = arguments.nargs;
    final List<Expression> args = arguments.getArgs();

    assert nargs != 0;

    buf = buf.append('(');

    for (int i = 0; i < nargs - 1; i++) {
      buf = visit(args.get(i), buf).append(',');
    }

    buf = buf.append("new ").append(arguments.componentType.getName()).append("[]");
    buf = buf.append('{');

    if (args.size() > nargs - 1) buf = visit(args.get(nargs - 1), buf);

    for (int i = nargs; i < args.size(); i++) buf = visit(args.get(i), buf.append(','));

    buf = buf.append('}').append(')');
    return buf;
  }

  private StringBuilder visit (Expression expr, StringBuilder buf) {
    return ExpressionCodeGenerator.instance.visit(expr, buf);
  }

  private ArgumentsCodeGenerator() {}
}
