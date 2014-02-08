package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ForCondParser extends PackratParser {
  /* ForCond
   *  : [ Expression ]
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = ExpressionParser.getParser(CtClass.booleanType).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      expr = new BooleanLiteral(true);
    }

    return expr;
  }

  public static final ForCondParser parser = new ForCondParser();

  private ForCondParser() {}
}
