package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class StaticInitializerParser extends PackratParser {
  /* StaticInitializer
   *  : Block
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST block = BlockParser.parser.applyRule(reader, env);
    if(block.isFail()) {
      reader.setPos(pos);
      return new BadAST(block.getFailLog());
    }

    return new ClassInitializer((Block)block);
  }

  public static final StaticInitializerParser parser = new StaticInitializerParser();

  private StaticInitializerParser() {}
}

