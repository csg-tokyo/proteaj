package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.CtClass;

public class FieldBodyParser extends PackratParser<FieldBody> {
  /* FieldBody
   *  : Expression
   */
  @Override
  protected ParseResult<FieldBody> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = ExpressionParser.getParser(type, env).applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    return success(new FieldBody(expr.get()));
  }

  public static FieldBodyParser getParser(CtClass type) {
    return new FieldBodyParser(type);
  }

  private final CtClass type;

  private FieldBodyParser(CtClass type) {
    this.type = type;
  }
}

