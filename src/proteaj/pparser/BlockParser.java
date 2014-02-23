package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class BlockParser extends PackratParser<Block> {
 /* Block
  *  : '{' { BlockStatement } '}'
  */
  @Override
  protected ParseResult<Block> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // '{'
    ParseResult<String> lBrace = KeywordParser.getParser("{").applyRule(reader, env);
    if(lBrace.isFail()) return fail(lBrace, pos, reader);

    Block block = new Block();
    Environment newenv = new Environment(env);

    // { BlockStatement }
    ParseResult<Statement> stmt;
    while(true) {
      stmt = BlockStatementParser.parser.applyRule(reader, newenv);
      if(stmt.isFail()) break;

      block.addStatement(stmt.get());
    }

    // '}'
    ParseResult<String> rBrace = KeywordParser.getParser("}").applyRule(reader, env);
    if(rBrace.isFail()) return fail(stmt, pos, reader);   // rbrace's error message is not suitable.

    env.inheritExceptions(newenv);

    return success(block);
  }

  public static final BlockParser parser = new BlockParser();

  private BlockParser() {}
}
