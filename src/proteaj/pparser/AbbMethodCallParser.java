package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;
import static proteaj.util.CtClassUtil.*;

public class AbbMethodCallParser extends PackratParser<Expression> {
  /* AbbMethodCall
   *  : Identifier Arguments
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // Identifier
    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) return fail(identifier, pos, reader);

    String name = identifier.get();
    int apos = reader.getPos();

    // Arguments
    for(CtMethod method : getMethods(env.thisClass)) try {
      if(! method.getName().equals(name)) continue;
      if(isStatic(method.getModifiers()) || ! env.isStatic()) {
        ParseResult<Arguments> args = ArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
        if(args.isFail() && hasVarArgs(method.getModifiers())) {
          args = VariableArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
        }
        if(! args.isFail()) {
          env.addExceptions(method.getExceptionTypes(), reader.getLine());
          if(isStatic(method.getModifiers())) return success(new StaticMethodCall(method, args.get()));
          else return success(new MethodCall(new ThisExpression(env.thisClass), method, args.get()));
        }
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // TODO static import

    // fail
    return fail("undefined method : " + name, pos, reader);
  }

  @Override
  public String toString() {
    return "AbbMethodCallParser";
  }

  public static final AbbMethodCallParser parser = new AbbMethodCallParser();

  private AbbMethodCallParser() {}
}

