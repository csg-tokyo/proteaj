package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class LocalVarDeclParser extends PackratParser {
  /* LocalVarDecl
   *  : Type Identifier [ '=' Expression ]
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST type = TypeNameParser.parser.applyRule(reader, env);
    if(type.isFail()) {
      reader.setPos(pos);
      return new BadAST(type.getFailLog());
    }

    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    CtClass cttype = ((TypeName)type).getType();
    String name = ((Identifier)identifier).getName();

    int epos = reader.getPos();
    LocalVarDecl lvdecl;

    TypedAST eq = KeywordParser.getParser("=").applyRule(reader, env);
    if(eq.isFail()) {
      reader.setPos(epos);
      lvdecl = new LocalVarDecl(cttype, name);
    }
    else {
      TypedAST val = ExpressionParser.getParser(cttype).applyRule(reader, env);
      if(val.isFail()) {
        reader.setPos(pos);
        return new BadAST(val.getFailLog());
      }
      lvdecl = new LocalVarDecl(cttype, name, (Expression)val);
    }

    env.add(name, new LocalVariable(name, cttype));

    return lvdecl;
  }

  public static final LocalVarDeclParser parser = new LocalVarDeclParser();

  private LocalVarDeclParser() {}
}
