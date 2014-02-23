package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.SourceStringReader;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ArrayAccessParser extends PackratParser<Expression> {
  /* ArrayAccess
   *  : JavaExpression '[' Expression ']'
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    ParseResult<String> lBracket = KeywordParser.getParser("[").applyRule(reader, env);
    if(lBracket.isFail()) return fail(lBracket, pos, reader);

    ParseResult<Expression> index = ExpressionParser.getParser(CtClass.intType, env).applyRule(reader, env);
    if(index.isFail()) return fail(index, pos, reader);

    ParseResult<String> rBracket = KeywordParser.getParser("]").applyRule(reader, env);
    if(rBracket.isFail()) return fail(rBracket, pos, reader);

    if (expr.get().getType().isArray()) try {
      return success(new ArrayAccess(expr.get(), index.get()));
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
    }
    return fail("not array type", pos, reader);
  }

  public static final ArrayAccessParser parser = new ArrayAccessParser();

  private ArrayAccessParser() {}
}

