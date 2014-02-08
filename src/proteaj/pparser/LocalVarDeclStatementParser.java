package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class LocalVarDeclStatementParser extends PackratParser {
  /* LocalVarDeclStatement
   *  : LocalVarDecl ';'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lvdecl = LocalVarDeclParser.parser.applyRule(reader, env);
    if(lvdecl.isFail()) {
      reader.setPos(pos);
      return new BadAST(lvdecl.getFailLog());
    }

    TypedAST semicolon = KeywordParser.getParser(";").applyRule(reader, env);
    if(semicolon.isFail()) {
      reader.setPos(pos);
      return new BadAST(semicolon.getFailLog());
    }

    return new LocalVarDeclStatement((LocalVarDecl)lvdecl);
  }

  public static final LocalVarDeclStatementParser parser = new LocalVarDeclStatementParser();

  private LocalVarDeclStatementParser() {}
}

