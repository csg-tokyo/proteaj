package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class QualifiedIdentifierParser extends PackratParser {
  /* QualifiedIdentifier
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

    QualifiedIdentifier qid = new QualifiedIdentifier((Identifier)id);

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

      qid.append((Identifier)id);
    }

    return qid;
  }

  @Override
  public String toString() {
    return "QualifiedIdentifierParser";
  }

  public static final QualifiedIdentifierParser parser = new QualifiedIdentifierParser();

  private QualifiedIdentifierParser() {}
}