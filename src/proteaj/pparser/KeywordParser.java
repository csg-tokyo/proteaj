package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;

import java.util.*;

import static java.lang.Character.isWhitespace;

public class KeywordParser extends PackratParser<String> {

  @Override
  protected ParseResult<String> parse(SourceStringReader reader, Environment env) {
    assert keyword != null;

    if (keyword.isEmpty()) return success("");

    char k0 = keyword.charAt(0);

    int pos = reader.getPos();

    while(reader.hasNext()) {
      int ch = reader.lookahead();
      if(ch == k0) {
        reader.next();

        for(int i = 1; i < keyword.length(); i++) {
          if(reader.lookahead() == keyword.charAt(i)) reader.next();
          else return fail("can't found expected token : \"" + keyword + "\"", pos, reader);
        }

        // success
        return success(keyword);
      }
      else if(isWhitespace(ch)) reader.next();
      else break;
    }

    // fail
    return fail("can't found expected token : \"" + keyword + "\"", pos, reader);
  }

  public static KeywordParser getParser(String keyword) {
    if(parsers.containsKey(keyword)) return parsers.get(keyword);
    else {
      KeywordParser parser = new KeywordParser(keyword);
      parsers.put(keyword, parser);
      return parser;
    }
  }

  @Override
  public String toString() {
    return "KeywordParser" + "[" + keyword + "]";
  }

  private KeywordParser(String keyword) {
    this.keyword = keyword;
  }

  private String keyword;

  private static Map<String, KeywordParser> parsers = new HashMap<String, KeywordParser>();
}
