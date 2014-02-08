package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;

import static java.lang.Character.isWhitespace;

public class KeywordParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    assert keyword != null && (! keyword.isEmpty());

    char k0 = keyword.charAt(0);

    int pos = reader.getPos();

    while(reader.hasNext()) {
      int ch = reader.lookahead();
      if(ch == k0) {
        reader.next();

        for(int i = 1; i < keyword.length(); i++) {
          if(reader.lookahead() == keyword.charAt(i)) reader.next();
          else {
            // fail
            FailLog flog = new FailLog("can't found expected token : \"" + keyword + "\"", reader.getPos(), reader.getLine());
            reader.setPos(pos);
            return new BadAST(flog);
          }
        }

        // success
        return new Keyword(keyword);
      }
      else if(isWhitespace(ch)) {
        reader.next();
      }
      else break;
    }

    // fail
    reader.setPos(pos);
    FailLog flog = new FailLog("can't found expected token : \"" + keyword + "\"", reader.getPos(), reader.getLine());
    return new BadAST(flog);
  }

  public static KeywordParser getParser(String keyword) {
    if(parsers.containsKey(keyword)) return parsers.get(keyword);
    else {
      KeywordParser parser = new KeywordParser(keyword);
      parser.init();
      parsers.put(keyword, parser);
      return parser;
    }
  }

  public static void initAll() {
    for(KeywordParser parser : parsers.values()) {
      parser.init();
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
