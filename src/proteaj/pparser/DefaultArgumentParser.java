package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

public class DefaultArgumentParser extends PackratParser<DefaultValue> {

  @Override
  protected ParseResult<DefaultValue> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = ExpressionParser.getParser(type, env).applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    return success(new DefaultValue(expr.get()));
  }

  public static DefaultArgumentParser getParser(CtClass type) {
    return new DefaultArgumentParser(type);
  }

  private final CtClass type;

  private DefaultArgumentParser(CtClass type) {
    this.type = type;
  }
}

