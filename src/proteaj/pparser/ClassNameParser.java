package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;

import javassist.*;

public class ClassNameParser extends PackratParser<CtClass> {
  /* ClassName
   *  : Identifier { '.' Identifier }
   */
  @Override
  protected ParseResult<CtClass> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> id = IdentifierParser.parser.applyRule(reader, env);
    if(id.isFail()) return fail(id, pos, reader);

    String name = id.get();

    while(true) {
      final int dpos = reader.getPos();

      final ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);
      if(dot.isFail()) {
        reader.setPos(dpos);
        break;
      }

      id = IdentifierParser.parser.applyRule(reader, env);
      if(id.isFail()) {
        reader.setPos(dpos);
        break;
      }

      String lname = name + '.' + id.get();

      if(env.isTypeName(name) && (! env.isTypeName(lname))) {
        reader.setPos(dpos);
        break;
      }

      name = lname;
    }

    try {
      return success(env.getType(name));
    } catch (NotFoundError e) {
      return fail(e.getMessage(), pos, reader);
    }
  }

  @Override
  public String toString() {
    return "ClassNameParser";
  }

  public static final ClassNameParser parser = new ClassNameParser();

  private ClassNameParser() {}
}

