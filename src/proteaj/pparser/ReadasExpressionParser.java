package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

import static java.lang.Character.isWhitespace;

public class ReadasExpressionParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    while(isWhitespace(reader.lookahead())) reader.next();

    TypedAST operand = ReadasOperandParser.getParser(type).applyRule(reader, env);
    if(operand.isFail()) reader.setPos(pos);

    return operand;
  }

  public static ReadasExpressionParser getParser(CtClass type) {
    if (parsers.containsKey(type)) return parsers.get(type);

    ReadasExpressionParser parser = new ReadasExpressionParser(type);
    parsers.put(type, parser);
    return parser;
  }

  private ReadasExpressionParser(CtClass type) {
    this.type = type;
  }

  private CtClass type;

  private static Map<CtClass, ReadasExpressionParser> parsers = new HashMap<CtClass, ReadasExpressionParser>();
}

