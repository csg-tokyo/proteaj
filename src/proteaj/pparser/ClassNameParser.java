package proteaj.pparser;

import proteaj.TypeResolver;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ClassNameParser extends PackratParser {
  /* ClassName
   *  : Identifier { '.' Identifier }
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST id = IdentifierParser.parser.applyRule(reader, env);
    if(id.isFail()) {
      reader.setPos(pos);
      return new BadAST(id.getFailLog());
    }

    String name = ((Identifier)id).getName();


    while(true) {
      int dpos = reader.getPos();

      TypedAST dot = KeywordParser.getParser(".").applyRule(reader, env);
      if(dot.isFail()) {
        reader.setPos(dpos);
        break;
      }

      id = IdentifierParser.parser.applyRule(reader, env);
      if(id.isFail()) {
        reader.setPos(dpos);
        break;
      }

      String lname = name + '.' + ((Identifier)id).getName();

      if(resolver.isTypeName(name) && (! resolver.isTypeName(lname))) {
        reader.setPos(dpos);
        break;
      }

      name = lname;
    }

    try {
      CtClass cls = resolver.getType(name);
      return new ClassName(cls);
    } catch (NotFoundError e) {
      FailLog flog = new FailLog(e.getMessage(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }
  }

  public void init(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public String toString() {
    return "ClassNameParser";
  }

  public static final ClassNameParser parser = new ClassNameParser();

  private ClassNameParser() {}

  private TypeResolver resolver;
}

