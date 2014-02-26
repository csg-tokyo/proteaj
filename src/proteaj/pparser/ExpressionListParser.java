package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class ExpressionListParser extends PackratParser<ExpressionList> {
  /* ExpressionList
   *  : [ Expression { ',' Expression } ]
   */
  @Override
  protected ParseResult<ExpressionList> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    List<Expression> exprs = new ArrayList<Expression>();

    ParseResult<Expression> expr = ExpressionParser.getParser(CtClass.voidType, env).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return success(new ExpressionList(exprs));
    }

    exprs.add(expr.get());

    while(true) {
      int cpos = reader.getPos();

      ParseResult<String> comma = KeywordParser.getParser(",").applyRule(reader, env);
      if(comma.isFail()) {
        reader.setPos(cpos);
        break;
      }

      expr = ExpressionParser.getParser(CtClass.voidType, env).applyRule(reader, env);
      if(expr.isFail()) return fail(expr, pos, reader);

      exprs.add(expr.get());
    }

    return success(new ExpressionList(exprs));
  }

  public static final ExpressionListParser parser = new ExpressionListParser();

  private ExpressionListParser() {}
}

