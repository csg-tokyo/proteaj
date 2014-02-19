package proteaj.pparser;

import proteaj.ir.tast.*;

public class LocalVarDeclStatementParser extends ComposedParser_Sequential {
  /* LocalVarDeclStatement
   *  : LocalVarDecl ';'
   */
  private LocalVarDeclStatementParser() {
    super("LocalVarDeclStatementParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] { LocalVarDeclParser.parser, KeywordParser.getParser(";") };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    return new LocalVarDeclStatement((LocalVarDecl)as[0]);
  }

  public static final LocalVarDeclStatementParser parser = new LocalVarDeclStatementParser();
}

