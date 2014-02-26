package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class NewArrayExpressionParser extends PackratParser<NewArrayExpression> {
  /* NewArrayExpression
   *  : "new" ClassName '[' Expression ']' { '[' Expression ']' } { '[' ']' }
   */
  @Override
  protected ParseResult<NewArrayExpression> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "new"
    ParseResult<String> newKeyword = KeywordParser.getParser("new").applyRule(reader, env);
    if(newKeyword.isFail()) return fail(newKeyword, pos, reader);

    // ClassName
    ParseResult<CtClass> clsName = ClassNameParser.parser.applyRule(reader, env);
    if(clsName.isFail()) return fail(clsName, pos, reader);

    StringBuilder typeNameBuf = new StringBuilder(clsName.get().getName());
    List<Expression> args = new ArrayList<Expression>();

    // '['
    ParseResult<String> lBracket = KeywordParser.getParser("[").applyRule(reader, env);
    if(lBracket.isFail()) return fail(lBracket, pos, reader);

    // Expression
    ParseResult<Expression> arg = ExpressionParser.getParser(CtClass.intType, env).applyRule(reader, env);
    if(arg.isFail()) return fail(arg, pos, reader);

    args.add(arg.get());

    // ']'
    ParseResult<String> rBracket = KeywordParser.getParser("]").applyRule(reader, env);
    if(rBracket.isFail()) return fail(rBracket, pos, reader);

    typeNameBuf.append("[]");

    while(true) {
      int bpos = reader.getPos();

      // "["
      lBracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lBracket.isFail()) try {
        return success(new NewArrayExpression(env.getType(typeNameBuf.toString()), args));
      } catch (NotFoundError e) {
        return fail("unknown type : " + typeNameBuf.toString(), pos, reader);
      }

      // Expression
      arg = ExpressionParser.getParser(CtClass.intType, env).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(bpos);
        break;
      }

      args.add(arg.get());

      // "]"
      rBracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rBracket.isFail()) return fail(rBracket, pos, reader);

      typeNameBuf.append("[]");
    }

    while(true) {
      // "["
      lBracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lBracket.isFail()) try {
        return success(new NewArrayExpression(env.getType(typeNameBuf.toString()), args));
      } catch (NotFoundError e) {
        return fail("unknown type : " + typeNameBuf.toString(), pos, reader);
      }

      // "]"
      rBracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rBracket.isFail()) return fail(rBracket, pos, reader);

      typeNameBuf.append("[]");
    }
  }

  @Override
  public String toString() {
    return "NewArrayExpressionParser";
  }

  public static final NewArrayExpressionParser parser = new NewArrayExpressionParser();

  private NewArrayExpressionParser() {}
}

