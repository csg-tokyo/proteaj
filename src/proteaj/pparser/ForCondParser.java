package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ForCondParser extends PackratParser<Expression> {
  /* ForCond
   *  : [ Expression ]
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = ExpressionParser.getParser(CtClass.booleanType, env).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return success(new BooleanLiteral(true));
    }

    return expr;
  }

  public static final ForCondParser parser = new ForCondParser();

  private ForCondParser() {}
}
