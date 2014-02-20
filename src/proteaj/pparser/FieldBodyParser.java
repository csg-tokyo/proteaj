package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.CtClass;

public class FieldBodyParser extends PackratParser {
  /* FieldBody
   *  : Expression
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = ExpressionParser.getParser(type, env).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    return new FieldBody((Expression)expr);
  }

  public static FieldBodyParser getParser(CtClass type) {
    return new FieldBodyParser(type);
  }

  private final CtClass type;

  private FieldBodyParser(CtClass type) {
    this.type = type;
  }
}

