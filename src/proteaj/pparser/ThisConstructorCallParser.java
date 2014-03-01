package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class ThisConstructorCallParser extends PackratParser<ThisConstructorCall> {
  /* ThisConstructorCall
   *  : "this" Arguments ';'
   */
  @Override
  protected ParseResult<ThisConstructorCall> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> keyword = KeywordParser.getParser("this").applyRule(reader, env);
    if(keyword.isFail()) return fail(keyword, pos, reader);

    int apos = reader.getPos();

    for(CtConstructor c : env.thisClass.getDeclaredConstructors()) try {
      ParseResult<List<Expression>> args = ArgumentsParser.getParser(c).applyRule(reader, env, apos);
      if(args.isFail()) continue;

      ParseResult<String> semicolon = KeywordParser.getParser(";").applyRule(reader, env);
      if(semicolon.isFail()) return fail(semicolon, pos, reader);

      if(c == env.thisMember) return fail("recursive constructor invocation", pos, reader);

      env.addExceptions(c.getExceptionTypes(), reader.getLine());
      return success(new ThisConstructorCall(c, args.get()));
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
    }

    return fail("undefined constructor", pos, reader);
  }

  public static final ThisConstructorCallParser parser = new ThisConstructorCallParser();

  private ThisConstructorCallParser() {}
}

