package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ArrayAccessParser extends PackratParser {
  /* ArrayAccess
   *  : JavaExpression '[' Expression ']'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // JavaExpression
    TypedAST jexpr = JavaExpressionParser.parser.applyRule(reader, env);
    if(jexpr.isFail()) {
      reader.setPos(pos);
      return new BadAST(jexpr.getFailLog());
    }

    // '['
    TypedAST lbracket = KeywordParser.getParser("[").applyRule(reader, env);
    if(lbracket.isFail()) {
      reader.setPos(pos);
      return new BadAST(lbracket.getFailLog());
    }

    // Expression
    TypedAST index = ExpressionParser.getParser(CtClass.intType).applyRule(reader, env);
    if(index.isFail()) {
      reader.setPos(pos);
      return new BadAST(index.getFailLog());
    }

    // ']'
    TypedAST rbracket = KeywordParser.getParser("]").applyRule(reader, env);
    if(rbracket.isFail()) {
      reader.setPos(pos);
      return new BadAST(rbracket.getFailLog());
    }

    Expression expr = (Expression)jexpr;

    if(! expr.getType().isArray()) {
      FailLog flog = new FailLog("not array type", reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    try {
      return new ArrayAccess(expr, (Expression)index);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      FailLog flog = new FailLog("invalid array type", reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }
  }

  @Override
  public String toString() {
    return "ArrayAccessParser";
  }

  public static final ArrayAccessParser parser = new ArrayAccessParser();

  private ArrayAccessParser() {}
}

