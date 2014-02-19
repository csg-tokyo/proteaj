package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.hasVarArgs;

public class SuperConstructorCallParser extends PackratParser {
  /* SuperConstructorCall
   *  : "super" Arguments ';'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST keyword = KeywordParser.getParser("super").applyRule(reader, env);
    if(keyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(keyword.getFailLog());
    }

    int apos = reader.getPos();

    try {
      CtClass superCls = thisCls.getSuperclass();

      for(CtConstructor constructor : superCls.getDeclaredConstructors()) {
        if(! constructor.visibleFrom(thisCls)) continue;

        TypedAST args = ArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
        if(args.isFail() && hasVarArgs(constructor.getModifiers())) {
          args = VariableArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
        }

        if(args.isFail()) continue;

        TypedAST semicolon = KeywordParser.getParser(";").applyRule(reader, env);
        if(semicolon.isFail()) {
          reader.setPos(pos);
          return new BadAST(semicolon.getFailLog());
        }

        env.addExceptions(constructor.getExceptionTypes(), reader.getLine());
        return new SuperConstructorCall(constructor, (Arguments)args);
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    FailLog flog = new FailLog("undefined super constructor", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtClass thisCls) {
    this.thisCls = thisCls;
  }

  public static final SuperConstructorCallParser parser = new SuperConstructorCallParser();

  private CtClass thisCls;

  private SuperConstructorCallParser() {}
}

