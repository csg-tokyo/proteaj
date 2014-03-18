package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.util.CtClassUtil.*;

public class ProteaJLeftArrowCastParser extends PackratParser<CastExpression> {
  /* ProteaJLeftArrowCast
   *  : '(' [ TypeName ] "<-" TypeName ')' Expression
   */
  @Override
  protected ParseResult<CastExpression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<String> lPar = KeywordParser.getParser("(").applyRule(reader, env);
    if (lPar.isFail()) return fail(lPar, pos, reader);

    int tPos = reader.getPos();

    ParseResult<CtClass> to = CommonParsers.typeName.applyRule(reader, env);
    if (to.isFail()) reader.setPos(tPos);
    else if (! isSubtype(to.get(), type, reader.filePath, reader.getLine())) {
      return fail("type mismatch: expected " + type.getName() + " but found " + to.get().getName(), pos, reader);
    }

    ParseResult<String> arrow = KeywordParser.getParser("<-").applyRule(reader, env);
    if (arrow.isFail()) return fail(arrow, pos, reader);

    ParseResult<CtClass> from = CommonParsers.typeName.applyRule(reader, env);
    if (from.isFail()) return fail(from, pos, reader);

    ParseResult<String> rPar = KeywordParser.getParser(")").applyRule(reader, env);
    if (rPar.isFail()) return fail(rPar, pos, reader);

    CtClass source = from.get();
    CtClass target = to.getOrElse(type);

    ParseResult<Expression> expr = ExpressionParser.getParser(source, env).applyRule(reader, env);
    if (expr.isFail()) return fail(expr, pos, reader);

    if (! isCastable(source, target, reader.filePath, reader.getLine())) {
      return fail(source.getName() + " cannot cast to " + target.getName(), pos, reader);
    }

    return success(new CastExpression(target, expr.get()));
  }

  public static ProteaJLeftArrowCastParser getParser(CtClass type) {
    if (! parsers.containsKey(type)) parsers.put(type, new ProteaJLeftArrowCastParser(type));
    return parsers.get(type);
  }

  private static Map<CtClass, ProteaJLeftArrowCastParser> parsers = new HashMap<CtClass, ProteaJLeftArrowCastParser>();

  private ProteaJLeftArrowCastParser(CtClass type) { this.type = type; }

  private final CtClass type;
}
