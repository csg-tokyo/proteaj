package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;

public class StaticFieldAccessParser extends PackratParser {
  /* StaticFieldAccess
   *  : ClassName '.' Identifier
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // ClassName
    TypedAST className = ClassNameParser.parser.applyRule(reader, env);
    if(className.isFail()) {
      reader.setPos(pos);
      return new BadAST(className.getFailLog());
    }

    // '.'
    TypedAST dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) {
      reader.setPos(pos);
      return new BadAST(dot.getFailLog());
    }

    // Identifier
    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    CtClass ctcls = ((ClassName)className).getCtClass();
    String name = ((Identifier)identifier).getName();

    if(ctcls == env.thisClass) {
      for(CtField field : ctcls.getDeclaredFields()) {
        if(isStatic(field.getModifiers()) && field.getName().equals(name)) try {
          return new StaticFieldAccess(field);
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
          break;
        }
      }
    }

    for(CtField field : ctcls.getFields()) {
      if(isStatic(field.getModifiers()) && field.visibleFrom(env.thisClass) && field.getName().equals(name)) try {
        return new StaticFieldAccess(field);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
        break;
      }
    }

    // fail
    FailLog flog = new FailLog("undefined field : " + name, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return "StaticFieldAccessParser";
  }

  public static final StaticFieldAccessParser parser = new StaticFieldAccessParser();

  private StaticFieldAccessParser() {}
}

