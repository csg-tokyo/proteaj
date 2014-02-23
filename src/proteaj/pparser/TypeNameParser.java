package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;

import javassist.*;

public class TypeNameParser extends PackratParser<CtClass> {
  /* TypeName
   *  : QualifiedIdentifier { '[' ']' }
   */
  @Override
  protected ParseResult<CtClass> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> qid = QualifiedIdentifierParser.parser.applyRule(reader, env);
    if(qid.isFail()) return fail(qid, pos, reader);

    int dim = 0;

    while(true) {
      int lpos = reader.getPos();

      ParseResult<String> lBracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lBracket.isFail()) {
        reader.setPos(lpos);
        break;
      }

      ParseResult<String> rBracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rBracket.isFail()) {
        reader.setPos(lpos);
        break;
      }

      dim++;
    }

    StringBuilder buf = new StringBuilder(qid.get());
    for(int i = 0; i < dim; i++) buf.append("[]");

    String typename = buf.toString();

    try {
      return success(env.getType(typename));
    } catch (NotFoundError e) {
      return fail(e.getMessage(), pos, reader);
    }
  }

  public static final TypeNameParser parser = new TypeNameParser();

  private TypeNameParser() {}
}
