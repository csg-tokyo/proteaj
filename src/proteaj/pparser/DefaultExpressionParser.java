package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class DefaultExpressionParser extends ExpressionParser {
  /* DefaultExpression
   *  : '(' Expression ')'
   *  | JavaExpression
   *  | "null"
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    FailLog flog = null;

    TypedAST lbrace = KeywordParser.getParser("(").applyRule(reader, env);
    if(! lbrace.isFail()) {
      TypedAST expr = ExpressionParser.getParser(type).applyRule(reader, env);
      if(! expr.isFail()) {
        TypedAST rbrace = KeywordParser.getParser(")").applyRule(reader, env);
        if(! rbrace.isFail()) {
          return expr;
        }
        else flog = rbrace.getFailLog();
      }
      else flog = expr.getFailLog();
    }
    else flog = lbrace.getFailLog();

    reader.setPos(pos);
    TypedAST expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(! expr.isFail()) try {
      CtClass exprType = ((Expression)expr).getType();

      if(exprType.subtypeOf(type) || type == CtClass.voidType) return expr;

      FailLog f = new FailLog("type mismatch: expected " + type.getName() + " but found " + exprType.getName(), reader.getPos(), reader.getLine());
      expr = new BadAST(f);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    reader.setPos(pos);
    TypedAST nll = KeywordParser.getParser("null").applyRule(reader, env);
    if(! nll.isFail()) {
      if(! type.isPrimitive()) return NullLiteral.instance;

      FailLog f = new FailLog("type mismatch: expected " + type.getName() + " but found null", reader.getPos(), reader.getLine());
      nll = new BadAST(f);
    }

    reader.setPos(pos);
    TypedAST literal = ReadasExpressionParser.getParser(type).applyRule(reader, env);
    if(! literal.isFail()) {
      return literal;
    }

    flog = chooseBest(flog, expr.getFailLog(), nll.getFailLog(), literal.getFailLog());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public static void initAll() {
    for(DefaultExpressionParser parser : parsers.values()) {
      parser.init();
    }
  }

  public static DefaultExpressionParser getParser(CtClass type) {
    if(parsers.containsKey(type)) return parsers.get(type);

    DefaultExpressionParser parser = new DefaultExpressionParser(type);
    parser.init();
    parsers.put(type, parser);
    return parser;
  }

  private static Map<CtClass, DefaultExpressionParser> parsers = new HashMap<CtClass, DefaultExpressionParser>();

  private DefaultExpressionParser(CtClass type) {
    super(type);
  }
}