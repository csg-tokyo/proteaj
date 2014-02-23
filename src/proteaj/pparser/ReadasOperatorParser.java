package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;

import java.util.*;

public class ReadasOperatorParser extends PackratParser<String> {
  @Override
  protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
    assert keyword != null;

    if (keyword.isEmpty()) return success("");

    final int pos = reader.getPos();

    for(int i = 0; i < keyword.length(); i++) {
      if(reader.lookahead() == keyword.charAt(i)) reader.next();
      else return fail("can't found expected token : \"" + keyword + "\"", pos, reader);
    }

    return success(keyword);
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

