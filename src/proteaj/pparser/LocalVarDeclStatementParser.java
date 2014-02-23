package proteaj.pparser;

import proteaj.io.SourceStringReader;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class LocalVarDeclStatementParser extends PackratParser<Statement> {
  /* LocalVarDeclStatement
   *  : LocalVarDecl ';'
   */
  @Override
  protected ParseResult<Statement> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<LocalVarDecl> local = LocalVarDeclParser.parser.applyRule(reader, env);
    if (local.isFail()) return fail(local, pos, reader);

    ParseResult<String> semiColon = KeywordParser.getParser(";").applyRule(reader, env);
    if (semiColon.isFail()) return fail(semiColon, pos, reader);

    return success(new LocalVarDeclStatement(local.get()));
  }

  public static final LocalVarDeclStatementParser parser = new LocalVarDeclStatementParser();

  private LocalVarDeclStatementParser() {}
}

