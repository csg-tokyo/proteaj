package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class VariableParser extends PackratParser<Expression> {
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) return fail(identifier, pos, reader);

    if(env.contains(identifier.get())) return success(env.get(identifier.get()));
    else return fail("unknown variable : " + identifier.get(), pos, reader);
  }

  @Override
  public String toString() {
    return "VariableParser";
  }

  public static final VariableParser parser = new VariableParser();

  private VariableParser() {}
}

