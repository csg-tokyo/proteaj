package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;

public class QualifiedIdentifierParser extends PackratParser<String> {
  /* QualifiedIdentifier
   *  : Identifier { '.' Identifier }
   */
  @Override
  protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> id = IdentifierParser.parser.applyRule(reader, env);
    if(id.isFail()) return fail(id, pos, reader);

    StringBuilder qid = new StringBuilder(id.get());

    while(true) {
      int dpos = reader.getPos();
      ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);

      if(dot.isFail()) {
        reader.setPos(dpos);
        break;
      }

      id = IdentifierParser.parser.applyRule(reader, env);
      if(id.isFail()) {
        reader.setPos(dpos);
        break;
      }

      qid.append('.').append(id.get());
    }

    return success(qid.toString());
  }

  @Override
  public String toString() {
    return "QualifiedIdentifierParser";
  }

  public static final QualifiedIdentifierParser parser = new QualifiedIdentifierParser();

  private QualifiedIdentifierParser() {}
}