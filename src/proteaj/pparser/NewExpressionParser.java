package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;

public class NewExpressionParser extends PackratParser<NewExpression> {
  /* NewExpression
   *  : "new" ClassName Arguments
   */
  @Override
  protected ParseResult<NewExpression> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "new"
    ParseResult<String> newKeyword = KeywordParser.getParser("new").applyRule(reader, env);
    if(newKeyword.isFail()) return fail(newKeyword, pos, reader);

    // ClassName
    ParseResult<CtClass> clsName = ClassNameParser.parser.applyRule(reader, env);
    if(clsName.isFail()) return fail(clsName, pos, reader);

    CtClass type = clsName.get();
    int apos = reader.getPos();

    // Arguments
    for(CtConstructor constructor : type.getDeclaredConstructors()) try {
      if(! constructor.visibleFrom(env.thisClass)) continue;

      ParseResult<Arguments> args = ArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(constructor.getModifiers())) {
        args = VariableArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(! args.isFail()) {
        env.addExceptions(constructor.getExceptionTypes(), reader.getLine());
        return success(new NewExpression(constructor, args.get()));
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    return fail("undefined constructor", pos, reader);
  }

  @Override
  public String toString() {
    return "NewExpressionParser";
  }

  public static final NewExpressionParser parser = new NewExpressionParser();

  private NewExpressionParser() {}
}

