package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.CtClassUtil.*;

public class ConstructorBodyParser extends PackratParser {
  /* ConstructorBody
   *  : '{' [ ThisConstructorCall | SuperConstructorCall ] { BlockStatement } '}'
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

    int bpos = reader.getPos();
    Block block = new Block();

    FailLog flog = null;

    // [ ThisConstructorCall | SuperConstructorCall ]
    TypedAST thisstmt = ThisConstructorCallParser.parser.applyRule(reader, env, bpos);
    if(! thisstmt.isFail()) block.addStatement((Statement)thisstmt);

    else {
      flog = thisstmt.getFailLog();
      TypedAST superstmt = SuperConstructorCallParser.parser.applyRule(reader, env, bpos);
      if(! superstmt.isFail()) block.addStatement((Statement)superstmt);
      else try {
        flog = chooseBest(flog, superstmt.getFailLog());

        CtClass superCls = env.thisClass.getSuperclass();
        if(! hasDefaultConstructor(superCls)) {
          flog = chooseBest(new FailLog("implicit super constructor is undefined. Must explicitly invoke another constructor", reader.getPos(), reader.getLine()), flog);
          reader.setPos(pos);
          flog = chooseBest(flog, thisstmt.getFailLog(), superstmt.getFailLog());
          return new BadAST(flog);
        }
        else if(! getDefaultConstructor(superCls).visibleFrom(env.thisClass)) {
          flog = chooseBest(new FailLog("implicit super constructor is not visible. Must explicitly invoke another constructor", reader.getPos(), reader.getLine()), flog);
          reader.setPos(pos);
          return new BadAST(flog);
        }
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }
    }


    TypedAST stmt;

    // { BlockStatement }
    while(true) {
      stmt = BlockStatementParser.parser.applyRule(reader, env);
      if(stmt.isFail()) break;

      block.addStatement((Statement)stmt);
    }

    // '}'
    TypedAST rbrace = KeywordParser.getParser("}").applyRule(reader, env);
    if(rbrace.isFail()) {
      if(flog == null) flog = chooseBest(stmt.getFailLog(), rbrace.getFailLog());
      else flog = chooseBest(flog, stmt.getFailLog(), rbrace.getFailLog());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    return new ConstructorBody(block);
  }

  public static final ConstructorBodyParser parser = new ConstructorBodyParser();

  private ConstructorBodyParser() {}
}

