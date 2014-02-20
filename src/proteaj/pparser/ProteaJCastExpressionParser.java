package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.util.CtClassUtil.*;

public class ProteaJCastExpressionParser extends PackratParser {
  /* ProteaJCastExpression
   *  : '(' TypeName "->" [ TypeName ] ')' Expression
   *  | '(' [ TypeName ] "<-" TypeName ')' Expression
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
    if (lparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(lparen.getFailLog());
    }

    int lpos = reader.getPos();

    TypedAST left = TypeNameParser.parser.applyRule(reader, env);
    if (left.isFail()) {
      reader.setPos(lpos);

      TypedAST arrow = KeywordParser.getParser("<-").applyRule(reader, env);
      if (arrow.isFail()) {
        reader.setPos(pos);
        return new BadAST(arrow.getFailLog());
      }

      TypedAST right = TypeNameParser.parser.applyRule(reader, env);
      if (right.isFail()) {
        reader.setPos(pos);
        return new BadAST(right.getFailLog());
      }

      TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
      if (rparen.isFail()) {
        reader.setPos(pos);
        return new BadAST(rparen.getFailLog());
      }

      CtClass from = ((TypeName)right).getType();

      TypedAST expr = ExpressionParser.getParser(from, env).applyRule(reader, env);
      if (expr.isFail()) {
        reader.setPos(pos);
        return new BadAST(expr.getFailLog());
      }

      if (! isCastable(from, type, reader.getFilePath(), reader.getLine())) {
        FailLog flog = new FailLog(from.getName() + " cannot cast to " + type.getName(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      return new CastExpression(type, (Expression)expr);
    }

    int apos = reader.getPos();

    TypedAST arrow = KeywordParser.getParser("->").applyRule(reader, env);
    if (arrow.isFail()) {
      reader.setPos(apos);

      arrow = KeywordParser.getParser("<-").applyRule(reader, env);
      if (arrow.isFail()) {
        reader.setPos(pos);
        return new BadAST(arrow.getFailLog());
      }

      TypedAST right = TypeNameParser.parser.applyRule(reader, env);
      if (right.isFail()) {
        reader.setPos(pos);
        return new BadAST(right.getFailLog());
      }

      TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
      if (rparen.isFail()) {
        reader.setPos(pos);
        return new BadAST(rparen.getFailLog());
      }

      CtClass from = ((TypeName)right).getType();
      CtClass to = ((TypeName)left).getType();

      TypedAST expr = ExpressionParser.getParser(from, env).applyRule(reader, env);
      if (expr.isFail()) {
        reader.setPos(pos);
        return new BadAST(expr.getFailLog());
      }

      if (! isCastable(from, to, reader.getFilePath(), reader.getLine())) {
        FailLog flog = new FailLog(from.getName() + " cannot cast to " + to.getName(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      if (! isSubtype(to, type, reader.getFilePath(), reader.getLine())) {
        FailLog flog = new FailLog("type mismatch: expected " + type.getName() + " but found " + to.getName(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      return new CastExpression(to, (Expression)expr);
    }

    int rpos = reader.getPos();

    TypedAST right = TypeNameParser.parser.applyRule(reader, env);
    if (right.isFail()) {
      reader.setPos(rpos);

      TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
      if (rparen.isFail()) {
        reader.setPos(pos);
        return new BadAST(rparen.getFailLog());
      }

      CtClass from = ((TypeName)left).getType();

      TypedAST expr = ExpressionParser.getParser(from, env).applyRule(reader, env);
      if (expr.isFail()) {
        reader.setPos(pos);
        return new BadAST(expr.getFailLog());
      }

      if (! isCastable(from, type, reader.getFilePath(), reader.getLine())) {
        FailLog flog = new FailLog(from.getName() + " cannot cast to " + type.getName(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      return new CastExpression(type, (Expression)expr);
    }

    TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
    if (rparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(rparen.getFailLog());
    }

    CtClass from = ((TypeName)left).getType();
    CtClass to = ((TypeName)right).getType();

    TypedAST expr = ExpressionParser.getParser(from, env).applyRule(reader, env);
    if (expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    if (! isCastable(from, to, reader.getFilePath(), reader.getLine())) {
      FailLog flog = new FailLog(from.getName() + " cannot cast to " + to.getName(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    if (! isSubtype(to, type, reader.getFilePath(), reader.getLine())) {
      FailLog flog = new FailLog("type mismatch: expected " + type.getName() + " but found " + to.getName(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    return new CastExpression(to, (Expression)expr);
  }

  public static ProteaJCastExpressionParser getParser(CtClass type) {
    if(parsers.containsKey(type)) return parsers.get(type);

    ProteaJCastExpressionParser parser = new ProteaJCastExpressionParser(type);
    parsers.put(type, parser);
    return parser;
  }

  private static Map<CtClass, ProteaJCastExpressionParser> parsers = new HashMap<CtClass, ProteaJCastExpressionParser>();

  private ProteaJCastExpressionParser(CtClass type) {
    this.type = type;
  }

  private CtClass type;
}
