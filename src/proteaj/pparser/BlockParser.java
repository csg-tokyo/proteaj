package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class BlockParser extends PackratParser {
 /* Block
  *  : '{' { BlockStatement } '}'
  */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // '{'
    TypedAST lbrace = KeywordParser.getParser("{").applyRule(reader, env);
    if(lbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(lbrace.getFailLog());
    }

    Block block = new Block();
    Environment newenv = new Environment(env);

    // { BlockStatement }
    TypedAST stmt;
    while(true) {
      stmt = BlockStatementParser.parser.applyRule(reader, newenv);
      if(stmt.isFail()) break;

      block.addStatement((Statement)stmt);
    }

    // '}'
    TypedAST rbrace = KeywordParser.getParser("}").applyRule(reader, env);
    if(rbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(stmt.getFailLog());          // rbrace's error message is not suitable.
    }

    env.inheritExceptions(newenv);

    return block;
  }

  public static final BlockParser parser = new BlockParser();

  private BlockParser() {}
}
