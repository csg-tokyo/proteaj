package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;

public class StaticFieldAccessParser extends PackratParser<Expression> {
  /* StaticFieldAccess
   *  : ClassName '.' Identifier
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
    if(identifier.isFail()) return fail(identifier, pos, reader);

    if(className.get() == env.thisClass) {
      for(CtField field : className.get().getDeclaredFields()) {
        if(isStatic(field.getModifiers()) && field.getName().equals(identifier.get())) try {
          return success(new StaticFieldAccess(field));
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
          break;
        }
      }
    }

    for(CtField field : className.get().getFields()) {
      if(isStatic(field.getModifiers()) && field.visibleFrom(env.thisClass) && field.getName().equals(identifier.get())) try {
        return success(new StaticFieldAccess(field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
        break;
      }
    }

    // fail
    return fail("undefined field : " + identifier.get(), pos, reader);
  }

  @Override
  public String toString() {
    return "StaticFieldAccessParser";
  }

  public static final StaticFieldAccessParser parser = new StaticFieldAccessParser();

  private StaticFieldAccessParser() {}
}

