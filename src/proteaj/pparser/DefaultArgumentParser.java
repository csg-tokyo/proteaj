package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class DefaultArgumentParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = ExpressionParser.getParser(type, env).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    return new DefaultValue((Expression)expr);
  }

  public static DefaultArgumentParser getParser(CtClass type) {
    return new DefaultArgumentParser(type);
  }

  private final CtClass type;

  private DefaultArgumentParser(CtClass type) {
    this.type = type;
  }
}

