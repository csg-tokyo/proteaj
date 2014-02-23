package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class LocalVarDeclParser extends PackratParser<LocalVarDecl> {
  /* LocalVarDecl
   *  : Type Identifier [ '=' Expression ]
   */
  @Override
  protected ParseResult<LocalVarDecl> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<CtClass> type = TypeNameParser.parser.applyRule(reader, env);
    if(type.isFail()) return fail(type, pos, reader);

    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) return fail(identifier, pos, reader);

    final int epos = reader.getPos();
    LocalVarDecl lvdecl;

    ParseResult<String> eq = KeywordParser.getParser("=").applyRule(reader, env);
    if(eq.isFail()) {
      reader.setPos(epos);
      lvdecl = new LocalVarDecl(type.get(), identifier.get());
    }
    else {
      ParseResult<Expression> val = ExpressionParser.getParser(type.get(), env).applyRule(reader, env);
      if(val.isFail()) return fail(val, pos, reader);
      else lvdecl = new LocalVarDecl(type.get(), identifier.get(), val.get());
    }

    env.add(identifier.get(), new LocalVariable(identifier.get(), type.get()));

    return success(lvdecl);
  }

  public static final LocalVarDeclParser parser = new LocalVarDeclParser();

  private LocalVarDeclParser() {}
}
