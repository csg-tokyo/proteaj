package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class TryStatementParser extends PackratParser {
  /* TryStatement
   *  : "try" Block { "catch" '(' ClassName Identifier ')' Block }+ [ "finally" Block ]
   *  | "try" Block "finally" Block
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST tryKeyword = KeywordParser.getParser("try").applyRule(reader, env);
    if(tryKeyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(tryKeyword.getFailLog());
    }

    Environment tryenv = new Environment(env);

    TypedAST tryBlock = BlockParser.parser.applyRule(reader, tryenv);
    if(tryBlock.isFail()) {
      reader.setPos(pos);
      return new BadAST(tryBlock.getFailLog());
    }

    int cpos = reader.getPos();
    TryStatement stmt = new TryStatement((Block)tryBlock);

    TypedAST finallyKeyword = KeywordParser.getParser("finally").applyRule(reader, env);
    if(! finallyKeyword.isFail()) {
      env.inheritExceptions(tryenv);

      TypedAST finallyBlock = BlockParser.parser.applyRule(reader, env);
      if(finallyBlock.isFail()) {
        reader.setPos(pos);
        return new BadAST(finallyBlock.getFailLog());
      }

      stmt.setFinallyBlock((Block)finallyBlock);

      return stmt;
    }

    reader.setPos(cpos);

    List<Environment> envs = new ArrayList<Environment>();

    while(true) {
      TypedAST catchKeyword = KeywordParser.getParser("catch").applyRule(reader, env);
      if(catchKeyword.isFail()) {
        if(stmt.hasCatchBlock()) break;
        reader.setPos(pos);
        return new BadAST(catchKeyword.getFailLog());
      }

      TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
      if(lparen.isFail()) {
        reader.setPos(pos);
        return new BadAST(lparen.getFailLog());
      }

      TypedAST type = ClassNameParser.parser.applyRule(reader, env);
      if(type.isFail()) {
        reader.setPos(pos);
        return new BadAST(type.getFailLog());
      }

      TypedAST name = IdentifierParser.parser.applyRule(reader, env);
      if(name.isFail()) {
        reader.setPos(pos);
        return new BadAST(name.getFailLog());
      }

      TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
      if(rparen.isFail()) {
        reader.setPos(pos);
        return new BadAST(rparen.getFailLog());
      }

      CtClass exceptionType = ((ClassName)type).getCtClass();
      String exceptionName = ((Identifier)name).getName();

      Environment newenv = new Environment(env);
      newenv.add(exceptionName, new LocalVariable(exceptionName, exceptionType));

      TypedAST catchBlock = BlockParser.parser.applyRule(reader, newenv);
      if(catchBlock.isFail()) {
        reader.setPos(pos);
        return new BadAST(catchBlock.getFailLog());
      }

      try {
        if(! exceptionType.subtypeOf(IRCommonTypes.getThrowableType())) {
          FailLog flog = new FailLog("No exception of type " + exceptionType.getName() + " can be thrown;" +
              " an exception type must be a subclass of Throwable", reader.getPos(), reader.getLine());
          reader.setPos(pos);
          return new BadAST(flog);
        }
        else tryenv.removeException(exceptionType);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }

      envs.add(newenv);

      stmt.addCatchBlock(exceptionType, exceptionName, (Block)catchBlock);
    }

    env.inheritExceptions(tryenv);
    for(Environment e : envs) env.inheritExceptions(e);

    int fpos = reader.getPos();

    finallyKeyword = KeywordParser.getParser("finally").applyRule(reader, env);
    if(! finallyKeyword.isFail()) {
      TypedAST finallyBlock = BlockParser.parser.applyRule(reader, env);
      if(finallyBlock.isFail()) {
        reader.setPos(pos);
        return new BadAST(finallyBlock.getFailLog());
      }

      stmt.setFinallyBlock((Block)finallyBlock);
    }
    else reader.setPos(fpos);

    return stmt;
  }

  public static final TryStatementParser parser = new TryStatementParser();

  private TryStatementParser() {}
}

