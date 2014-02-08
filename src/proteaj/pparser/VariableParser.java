package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class VariableParser extends PackratParser {
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // Identifier
    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    String name = ((Identifier)identifier).getName();

    if(env.contains(name)) {
      return env.get(name);
    }
    else {
      FailLog flog = new FailLog("unknown variable : " + name, reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }
  }

  @Override
  public String toString() {
    return "VariableParser";
  }

  public static final VariableParser parser = new VariableParser();

  private VariableParser() {}
}

