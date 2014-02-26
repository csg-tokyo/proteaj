package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class DefaultExpressionParser extends ExpressionParser {
  /* DefaultExpression
   *  : '(' Expression ')'
   *  | ProteaJCastExpression
   *  | JavaExpression
   *  | "null"
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    List<ParseResult<?>> fails = new ArrayList<ParseResult<?>>();

    ParseResult<Expression> parExpr = ParenthesizedExpressionParser.getParser(type).applyRule(reader, env, pos);
    if (! parExpr.isFail()) return parExpr;
    else fails.add(parExpr);

    ParseResult<CastExpression> cast = ProteaJCastExpressionParser.getParser(type).applyRule(reader, env, pos);
    if (! cast.isFail()) return success(cast.get());
    else fails.add(cast);

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(! expr.isFail()) try {
      CtClass exprType = expr.get().getType();

      if(exprType.subtypeOf(type) || type == CtClass.voidType) return expr;
      else {
        String msg = "type mismatch: expected " + type.getName() + " but found " + exprType.getName();
        fails.add(new Failure<Expression>(msg, reader.getPos(), reader.getLine()));
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }
    else fails.add(expr);

    ParseResult<String> nll = KeywordParser.getParser("null").applyRule(reader, env, pos);
    if(! nll.isFail()) {
      if(! type.isPrimitive()) return success(NullLiteral.instance);
      else {
        String msg = "type mismatch: expected " + type.getName() + " but found null";
        fails.add(new Failure<Expression>(msg, reader.getPos(), reader.getLine()));
      }
    }
    else fails.add(nll);

    ParseResult<Expression> literal = ReadasExpressionParser.getParser(type).applyRule(reader, env, pos);
    if(! literal.isFail()) return literal;
    else fails.add(literal);

    return fail(fails, pos, reader);
  }

  public static DefaultExpressionParser getParser(CtClass type) {
    if(parsers.containsKey(type)) return parsers.get(type);

    DefaultExpressionParser parser = new DefaultExpressionParser(type);
    parsers.put(type, parser);
    return parser;
  }

  private static Map<CtClass, DefaultExpressionParser> parsers = new HashMap<CtClass, DefaultExpressionParser>();

  private DefaultExpressionParser(CtClass type) {
    super(type);
  }
}