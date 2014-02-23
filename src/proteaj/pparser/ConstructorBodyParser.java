package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.util.CtClassUtil.*;

public class ConstructorBodyParser extends PackratParser<ConstructorBody> {
  /* ConstructorBody
   *  : '{' [ ThisConstructorCall | SuperConstructorCall ] { BlockStatement } '}'
   */
  @Override
  protected ParseResult<ConstructorBody> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // '{'
    ParseResult<String> lBrace = KeywordParser.getParser("{").applyRule(reader, env);
    if(lBrace.isFail()) return fail(lBrace, pos, reader);

    int bpos = reader.getPos();
    Block block = new Block();

    // [ ThisConstructorCall | SuperConstructorCall ]
    ParseResult<ThisConstructorCall> thisstmt = ThisConstructorCallParser.parser.applyRule(reader, env, bpos);
    if(! thisstmt.isFail()) block.addStatement(thisstmt.get());

    else {
      ParseResult<SuperConstructorCall> superstmt = SuperConstructorCallParser.parser.applyRule(reader, env, bpos);
      if(! superstmt.isFail()) block.addStatement(superstmt.get());
      else try {
        CtClass superCls = env.thisClass.getSuperclass();
        if(! hasDefaultConstructor(superCls)) {
          Failure<Statement> f = new Failure<Statement>("implicit super constructor is undefined. Must explicitly invoke another constructor", pos, reader.getLine(pos));
          List<ParseResult<?>> fails = Arrays.<ParseResult<?>>asList(f, thisstmt, superstmt);
          return fail(fails, pos, reader);
        }
        else if(! getDefaultConstructor(superCls).visibleFrom(env.thisClass)) {
          Failure<Statement> f = new Failure<Statement>("implicit super constructor is not visible. Must explicitly invoke another constructor", pos, reader.getLine(pos));
          List<ParseResult<?>> fails = Arrays.<ParseResult<?>>asList(f, thisstmt, superstmt);
          return fail(fails, pos, reader);
        }
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }
    }


    ParseResult<Statement> stmt;

    // { BlockStatement }
    while(true) {
      stmt = BlockStatementParser.parser.applyRule(reader, env);
      if(stmt.isFail()) break;

      block.addStatement(stmt.get());
    }

    // '}'
    ParseResult<String> rBrace = KeywordParser.getParser("}").applyRule(reader, env);
    if(rBrace.isFail()) return fail(stmt, pos, reader);

    return success(new ConstructorBody(block));
  }

  public static final ConstructorBodyParser parser = new ConstructorBodyParser();

  private ConstructorBodyParser() {}
}

