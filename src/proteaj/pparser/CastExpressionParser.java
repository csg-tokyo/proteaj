package proteaj.pparser;

import javassist.CtClass;
import proteaj.TypeResolver;
import proteaj.error.*;
import proteaj.ir.tast.*;

import static proteaj.util.CtClassUtil.*;

public class CastExpressionParser extends ComposedParser_Sequential {
  /* CastExpression
   *  : '(' TypeName ')' JavaExpression
   */
  private CastExpressionParser() {
    super("CastExpressionParser");
  }

  @Override
  protected PackratParser[] getParsers() {
    return new PackratParser[] {
        KeywordParser.getParser("("),
        TypeNameParser.parser,
        KeywordParser.getParser(")"),
        JavaExpressionParser.parser
    };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    CtClass to = ((TypeName)as[1]).getType();
    Expression e = (Expression)as[3];

    if (isCastable(e.getType(), to, file, line)) return new CastExpression(to, e);
    else return new BadAST(new FailLog(e.getType().getName() + " cannot cast to " + to.getName(), pos, line));
  }

  public void init(TypeResolver resolver) {
    this.resolver = resolver;
  }

  public static final CastExpressionParser parser = new CastExpressionParser();

  private TypeResolver resolver;
}
