package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class ExpressionListParser extends PackratParser {
  /* ExpressionList
   *  : [ Expression { ',' Expression } ]
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    List<Expression> exprs = new ArrayList<Expression>();

    TypedAST expr = ExpressionParser.getParser(CtClass.voidType, env).applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new ExpressionList(exprs);
    }

    exprs.add((Expression)expr);

    while(true) {
      int cpos = reader.getPos();

      TypedAST comma = KeywordParser.getParser(",").applyRule(reader, env);
      if(comma.isFail()) {
        reader.setPos(cpos);
        break;
      }

      expr = ExpressionParser.getParser(CtClass.voidType, env).applyRule(reader, env);
      if(expr.isFail()) {
        reader.setPos(pos);
        return new BadAST(expr.getFailLog());
      }

      exprs.add((Expression)expr);
    }

    return new ExpressionList(exprs);
  }

  public static final ExpressionListParser parser = new ExpressionListParser();

  private ExpressionListParser() {}
}

