package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class DefaultReadasOperandParser extends ReadasOperandParser {
  /* DefaultReadasOperand
   *  : '(' ReadasOperand ')'
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> lPar = ReadasOperatorParser.getParser("(").applyRule(reader, env);
    if(! lPar.isFail()) {
      ParseResult<Expression> expr = ReadasOperandParser.getParser(type, env).applyRule(reader, env);
      if(! expr.isFail()) {
        ParseResult<String> rPar = ReadasOperatorParser.getParser(")").applyRule(reader, env);
        if(! rPar.isFail()) {
          return expr;
        }
      }
    }

    reader.setPos(pos);

    StringBuilder buf = new StringBuilder();

    while(reader.hasNext()) {
      if(Character.isWhitespace(reader.lookahead())) break;
      buf.append(reader.next());
    }

    return fail("fail to parse the readas operand : " + buf.toString(), pos, reader);
  }

  public static DefaultReadasOperandParser getParser(CtClass type) {
    if(parsers.containsKey(type)) return parsers.get(type);

    DefaultReadasOperandParser parser = new DefaultReadasOperandParser(type);
    parsers.put(type, parser);
    return parser;
  }

  private static Map<CtClass, DefaultReadasOperandParser> parsers = new HashMap<CtClass, DefaultReadasOperandParser>();

  private DefaultReadasOperandParser(CtClass type) {
    super(type);
  }
}
