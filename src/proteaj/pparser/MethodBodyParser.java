package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class MethodBodyParser extends PackratParser {
  /* MethodBody
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

    return new MethodBody((Block)block);
  }

  public static final MethodBodyParser parser = new MethodBodyParser();

  private MethodBodyParser() {}
}
