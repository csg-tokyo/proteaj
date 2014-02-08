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

    TypedAST expr = ExpressionParser.getParser(type).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    return new FieldBody((Expression)expr);
  }

  public void init(CtClass type) {
    this.type = type;
    super.init();
  }

  public static final FieldBodyParser parser = new FieldBodyParser();

  private CtClass type;

  private FieldBodyParser() {}
}

