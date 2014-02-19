package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.hasVarArgs;

public class ThisConstructorCallParser extends PackratParser {
  /* ThisConstructorCall
   *  : "this" Arguments ';'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST keyword = KeywordParser.getParser("this").applyRule(reader, env);
    if(keyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(keyword.getFailLog());
    }

    int apos = reader.getPos();

    for(CtConstructor c : env.thisClass.getDeclaredConstructors()) try {
      TypedAST args = ArgumentsParser.getParser(c.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(c.getModifiers())) {
        args = VariableArgumentsParser.getParser(c.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(args.isFail()) continue;

      TypedAST semicolon = KeywordParser.getParser(";").applyRule(reader, env);
      if(semicolon.isFail()) {
        reader.setPos(pos);
        return new BadAST(semicolon.getFailLog());
      }

      if(c == constructor) {
        FailLog flog = new FailLog("recursive constructor invocation", reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      env.addExceptions(c.getExceptionTypes(), reader.getLine());
      return new ThisConstructorCall(c, (Arguments)args);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    FailLog flog = new FailLog("undefined constructor", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtConstructor constructor) {
    this.constructor = constructor;
  }

  public static final ThisConstructorCallParser parser = new ThisConstructorCallParser();

  private CtConstructor constructor;

  private ThisConstructorCallParser() {}
}

