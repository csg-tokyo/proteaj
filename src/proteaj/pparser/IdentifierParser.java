package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import static java.lang.Character.isWhitespace;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;

public class IdentifierParser extends PackratParser {
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    while(isWhitespace(reader.lookahead())) reader.next();

    if(! isJavaIdentifierStart(reader.lookahead())) {
      FailLog flog = new FailLog("expected identifier, but found " + (char)reader.lookahead(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    StringBuilder buf = new StringBuilder();
    buf.append(reader.next());

    while(reader.hasNext() && isJavaIdentifierPart(reader.lookahead())) {
      buf.append(reader.next());
    }

    return new Identifier(buf.toString());
  }

  @Override
  public String toString() {
    return "IdentifierParser";
  }

  public static final IdentifierParser parser = new IdentifierParser();

  private IdentifierParser() {}
}
