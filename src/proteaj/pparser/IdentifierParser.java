package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;

import static java.lang.Character.isWhitespace;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;

public class IdentifierParser extends PackratParser<String> {
  @Override
  protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    while(isWhitespace(reader.lookahead())) reader.next();

    if(! isJavaIdentifierStart(reader.lookahead())) {
      return fail("expected identifier, but found " + (char)reader.lookahead(), pos, reader);
    }

    StringBuilder buf = new StringBuilder();
    buf.append(reader.next());

    while(reader.hasNext() && isJavaIdentifierPart(reader.lookahead())) {
      buf.append(reader.next());
    }

    return success(buf.toString());
  }

  @Override
  public String toString() {
    return "IdentifierParser";
  }

  public static final IdentifierParser parser = new IdentifierParser();

  private IdentifierParser() {}
}
