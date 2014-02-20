package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class DefaultReadasOperandParser extends ReadasOperandParser {
  /* DefaultReadasOperand
   *  : '(' ReadasOperand ')'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lbrace = ReadasOperatorParser.getParser("(").applyRule(reader, env);
    if(! lbrace.isFail()) {
      TypedAST expr = ReadasOperandParser.getParser(type, env).applyRule(reader, env);
      if(! expr.isFail()) {
        TypedAST rbrace = ReadasOperatorParser.getParser(")").applyRule(reader, env);
        if(! rbrace.isFail()) {
          return expr;
        }
      }
    }

    reader.setPos(pos);
    int line = reader.getLine();

    StringBuilder buf = new StringBuilder();

    while(reader.hasNext()) {
      if(Character.isWhitespace(reader.lookahead())) break;
      buf.append(reader.next());
    }

    FailLog flog = new FailLog("fail to parse the readas operand : " + buf.toString(), pos, line);
    reader.setPos(pos);
    return new BadAST(flog);
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
