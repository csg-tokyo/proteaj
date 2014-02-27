package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.hasVarArgs;

public class SuperConstructorCallParser extends PackratParser<SuperConstructorCall> {
  /* SuperConstructorCall
   *  : "super" Arguments ';'
   */
  @Override
  protected ParseResult<SuperConstructorCall> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> keyword = KeywordParser.getParser("super").applyRule(reader, env);
    if(keyword.isFail()) return fail(keyword, pos, reader);

    int apos = reader.getPos();

    try {
      final CtClass superCls = env.thisClass.getSuperclass();

      for(CtConstructor constructor : superCls.getDeclaredConstructors()) {
        if(! constructor.visibleFrom(env.thisClass)) continue;

        ParseResult<Arguments> args = ArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
        if(args.isFail() && hasVarArgs(constructor.getModifiers())) {
          args = VariableArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
        }

        if(args.isFail()) continue;

        ParseResult<String> semicolon = KeywordParser.getParser(";").applyRule(reader, env);
        if(semicolon.isFail()) return fail(semicolon, pos, reader);

        env.addExceptions(constructor.getExceptionTypes(), reader.getLine());
        return success(new SuperConstructorCall(constructor, args.get()));
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
    }

    // fail
    return fail("undefined super constructor", pos, reader);
  }

  public static final SuperConstructorCallParser parser = new SuperConstructorCallParser();

  private SuperConstructorCallParser() {}
}

