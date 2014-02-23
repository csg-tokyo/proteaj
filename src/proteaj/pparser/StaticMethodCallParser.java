package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;
import static proteaj.util.CtClassUtil.*;

public class StaticMethodCallParser extends PackratParser<Expression> {
  /* StaticMethodCall
   *  : ClassName '.' Identifier Arguments
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // ClassName
    ParseResult<CtClass> className = ClassNameParser.parser.applyRule(reader, env);
    if(className.isFail()) return fail(className, pos, reader);

    // '.'
    ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) return fail(dot, pos, reader);

    // Identifier
    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) fail(identifier, pos, reader);

    int apos = reader.getPos();

    // Arguments
    for(CtMethod method : getMethods(className.get())) try {
      if(! (isStatic(method.getModifiers()) && method.visibleFrom(env.thisClass) && method.getName().equals(identifier.get()))) continue;

      ParseResult<Arguments> args = ArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(method.getModifiers())) {
        args = VariableArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(! args.isFail()) {
        env.addExceptions(method.getExceptionTypes(), reader.getLine());
        return success(new StaticMethodCall(method, args.get()));
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    return fail("undefined method : " + identifier.get(), pos, reader);
  }

  @Override
  public String toString() {
    return "StaticMethodCallParser";
  }

  public static final StaticMethodCallParser parser = new StaticMethodCallParser();

  private StaticMethodCallParser() {}
}

