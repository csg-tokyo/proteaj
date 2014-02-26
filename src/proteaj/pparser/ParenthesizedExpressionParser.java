package proteaj.pparser;

import javassist.CtClass;
import proteaj.io.SourceStringReader;
import proteaj.ir.Environment;
import proteaj.tast.Expression;

import java.util.HashMap;
import java.util.Map;

public class ParenthesizedExpressionParser extends PackratParser<Expression> {
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if(lPar.isFail()) return fail(lPar, pos, reader);

    ParseResult<Expression> expr = ExpressionParser.getParser(type, env).applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
    if(rPar.isFail()) return fail(rPar, pos, reader);

    return expr;
  }

  public static ParenthesizedExpressionParser getParser(CtClass type) {
    if (! parsers.containsKey(type)) parsers.put(type, new ParenthesizedExpressionParser(type));
    return parsers.get(type);
  }

  private static Map<CtClass, ParenthesizedExpressionParser> parsers = new HashMap<CtClass, ParenthesizedExpressionParser>();

  private final CtClass type;

  private ParenthesizedExpressionParser(CtClass type) { this.type = type; }
}
