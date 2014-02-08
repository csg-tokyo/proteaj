package proteaj.pparser;

import proteaj.TypeResolver;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class TypeNameParser extends PackratParser {
  /* TypeName
   *  : QualifiedIdentifier { '[' ']' }
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST qid = QualifiedIdentifierParser.parser.applyRule(reader, env);
    if(qid.isFail()) {
      reader.setPos(pos);
      return new BadAST(qid.getFailLog());
    }

    int dim = 0;

    while(true) {
      int lpos = reader.getPos();

      TypedAST lbracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lbracket.isFail()) {
        reader.setPos(lpos);
        break;
      }

      TypedAST rbracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rbracket.isFail()) {
        reader.setPos(lpos);
        break;
      }

      dim++;
    }

    StringBuilder buf = new StringBuilder(((QualifiedIdentifier)qid).toString());
    for(int i = 0; i < dim; i++) buf.append("[]");

    String typename = buf.toString();

    try {
      CtClass type = resolver.getType(typename);
      return new TypeName(type);
    } catch (NotFoundError e) {
      FailLog flog = new FailLog(e.getMessage(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }
  }

  public void init(TypeResolver resolver) {
    this.resolver = resolver;
    super.init();
  }

  public static final TypeNameParser parser = new TypeNameParser();

  private TypeNameParser() {}

  private TypeResolver resolver;
}
