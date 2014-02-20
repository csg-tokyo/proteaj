package proteaj.pparser;

import proteaj.error.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class ArrayAccessParser extends ComposedParser_Sequential {
  /* ArrayAccess
   *  : JavaExpression '[' Expression ']'
   */
  private ArrayAccessParser() {
    super("ArrayAccessParser");
  }

  @Override
  protected PackratParser[] getParsers(Environment env) {
    return new PackratParser[] {
        JavaExpressionParser.parser,
        KeywordParser.getParser("["),
        ExpressionParser.getParser(CtClass.intType, env),
        KeywordParser.getParser("]")
    };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    Expression expr = (Expression)as[0];
    Expression index = (Expression)as[2];
    if (expr.getType().isArray()) try {
      return new ArrayAccess(expr, index);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, file, line));
    }
    return new BadAST(new FailLog("not array type", pos, line));
  }

  public static final ArrayAccessParser parser = new ArrayAccessParser();
}

