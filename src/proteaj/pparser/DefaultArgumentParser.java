package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class DefaultArgumentParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST expr = ExpressionParser.getParser(type).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    return new DefaultValue((Expression)expr);
  }

  public void init(CtClass type) {
    this.type = type;
  }

  public static final DefaultArgumentParser parser = new DefaultArgumentParser();

  private CtClass type;

  private DefaultArgumentParser() {}
}

