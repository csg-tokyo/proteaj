package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class MethodBodyParser extends PackratParser<MethodBody> {
  /* MethodBody
   *  : Block
   */
  @Override
  protected ParseResult<MethodBody> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Block> block = BlockParser.parser.applyRule(reader, env);
    if(block.isFail()) return fail(block, pos, reader);

    return success(new MethodBody(block.get()));
  }

  public static final MethodBodyParser parser = new MethodBodyParser();

  private MethodBodyParser() {}
}
