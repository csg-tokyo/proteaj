package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class TryStatementParser extends PackratParser<Statement> {
  /* TryStatement
   *  : "try" Block { "catch" '(' ClassName Identifier ')' Block }+ [ "finally" Block ]
   *  | "try" Block "finally" Block
   */
  @Override
  protected ParseResult<Statement> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    ParseResult<String> tryKeyword = KeywordParser.getParser("try").applyRule(reader, env);
    if(tryKeyword.isFail()) return fail(tryKeyword, pos, reader);

    Environment tryenv = new Environment(env);

    ParseResult<Block> tryBlock = BlockParser.parser.applyRule(reader, tryenv);
    if(tryBlock.isFail()) return fail(tryBlock, pos, reader);

    int cpos = reader.getPos();
    TryStatement stmt = new TryStatement(tryBlock.get());

    ParseResult<String> finallyKeyword = KeywordParser.getParser("finally").applyRule(reader, env);
    if(! finallyKeyword.isFail()) {
      env.inheritExceptions(tryenv);

      ParseResult<Block> finallyBlock = BlockParser.parser.applyRule(reader, env);
      if(finallyBlock.isFail()) return fail(finallyBlock, pos, reader);

      stmt.setFinallyBlock(finallyBlock.get());

      return success(stmt);
    }

    reader.setPos(cpos);

    List<Environment> envs = new ArrayList<Environment>();

    while(true) {
      ParseResult<String> catchKeyword = KeywordParser.getParser("catch").applyRule(reader, env);
      if(catchKeyword.isFail()) {
        if(stmt.hasCatchBlock()) break;
        else return fail(catchKeyword, pos, reader);
      }

      ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
      if(lPar.isFail()) return fail(lPar, pos, reader);

      ParseResult<CtClass> type = ClassNameParser.parser.applyRule(reader, env);
      if(type.isFail()) return fail(type, pos, reader);

      ParseResult<String> name = IdentifierParser.parser.applyRule(reader, env);
      if(name.isFail()) return fail(name, pos, reader);

      ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
      if(rPar.isFail()) return fail(rPar, pos, reader);

      Environment newenv = new Environment(env);
      newenv.add(name.get(), new LocalVariable(name.get(), type.get()));

      ParseResult<Block> catchBlock = BlockParser.parser.applyRule(reader, newenv);
      if(catchBlock.isFail()) return fail(catchBlock, pos, reader);

      try {
        if(! type.get().subtypeOf(IRCommonTypes.getThrowableType())) {
          String msg = "No exception of type " + type.get().getName() + " can be thrown; an exception type must be a subclass of Throwable";
          return fail(msg, pos, reader);
        }
        else tryenv.removeException(type.get());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }

      envs.add(newenv);

      stmt.addCatchBlock(type.get(), name.get(), catchBlock.get());
    }

    env.inheritExceptions(tryenv);
    for(Environment e : envs) env.inheritExceptions(e);

    int fpos = reader.getPos();

    finallyKeyword = KeywordParser.getParser("finally").applyRule(reader, env);
    if(! finallyKeyword.isFail()) {
      ParseResult<Block> finallyBlock = BlockParser.parser.applyRule(reader, env);
      if(finallyBlock.isFail()) return fail(finallyBlock, pos, reader);
      else stmt.setFinallyBlock(finallyBlock.get());
    }
    else reader.setPos(fpos);

    return success(stmt);
  }

  public static final TryStatementParser parser = new TryStatementParser();

  private TryStatementParser() {}
}

