package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class StaticInitializerParser extends PackratParser<ClassInitializer> {
  /* StaticInitializer
   *  : Block
   */
  @Override
  protected ParseResult<ClassInitializer> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Block> block = BlockParser.parser.applyRule(reader, env);
    if(block.isFail()) return fail(block, pos, reader);

    return success(new ClassInitializer(block.get()));
  }

  public static final StaticInitializerParser parser = new StaticInitializerParser();

  private StaticInitializerParser() {}
}

