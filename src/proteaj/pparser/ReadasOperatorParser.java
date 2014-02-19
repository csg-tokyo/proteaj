package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;

public class ReadasOperatorParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    assert keyword != null && (! keyword.isEmpty());
    int pos = reader.getPos();

    for(int i = 0; i < keyword.length(); i++) {
      if(reader.lookahead() == keyword.charAt(i)) reader.next();
      else {
        // fail
        FailLog flog = new FailLog("can't found expected token : \"" + keyword + "\"", reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }
    }

    // success
    return new ReadasOperator(keyword);
  }

  public static ReadasOperatorParser getParser(String keyword) {
    if(parsers.containsKey(keyword)) return parsers.get(keyword);
    else {
      ReadasOperatorParser parser = new ReadasOperatorParser(keyword);
      parsers.put(keyword, parser);
      return parser;
    }
  }

  @Override
  public String toString() {
    return "ReadasOperatorParser" + "[" + keyword + "]";
  }

  private ReadasOperatorParser(String keyword) {
    this.keyword = keyword;
  }

  private String keyword;

  private static Map<String, ReadasOperatorParser> parsers = new HashMap<String, ReadasOperatorParser>();
}

