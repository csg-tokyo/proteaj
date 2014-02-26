package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;
import static proteaj.util.CtClassUtil.*;

public class MethodCallParser extends PackratParser<MethodCall> {
  /* MethodCall
   *  : JavaExpression '.' Identifier Arguments
   */
  @Override
  protected ParseResult<MethodCall> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // JavaExpression
    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    // '.'
    ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) return fail(dot, pos, reader);

    // Identifier
    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) return fail(identifier, pos, reader);

    int apos = reader.getPos();

    // Arguments
    for(CtMethod method : getMethods(expr.get().getType())) try {
      if(isStatic(method.getModifiers()) || ! method.visibleFrom(env.thisClass) || ! method.getName().equals(identifier.get())) continue;

      ParseResult<Arguments> args = ArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(method.getModifiers())) {
        args = VariableArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(! args.isFail()) {
        env.addExceptions(method.getExceptionTypes(), reader.getLine());
        return success(new MethodCall(expr.get(), method, args.get()));
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    return fail("undefined method : " + identifier.get(), pos, reader);
  }

  @Override
  public String toString() {
    return "MethodCallParser";
  }

  public static final MethodCallParser parser = new MethodCallParser();

  private MethodCallParser() {}
}

