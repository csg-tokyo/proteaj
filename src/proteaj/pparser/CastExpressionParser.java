package proteaj.pparser;

import javassist.CtClass;
import proteaj.TypeResolver;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import static proteaj.util.CtClassUtil.*;

public class CastExpressionParser extends PackratParser {
  /* CastExpression
   *  : '(' TypeName ')' JavaExpression
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lparen = KeywordParser.getParser("(").applyRule(reader, env);
    if(lparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(lparen.getFailLog());
    }

    TypedAST type = TypeNameParser.parser.applyRule(reader, env);
    if(type.isFail()) {
      reader.setPos(pos);
      return new BadAST(type.getFailLog());
    }

    TypedAST rparen = KeywordParser.getParser(")").applyRule(reader, env);
    if(rparen.isFail()) {
      reader.setPos(pos);
      return new BadAST(rparen.getFailLog());
    }

    TypedAST expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) {
      reader.setPos(pos);
      return new BadAST(expr.getFailLog());
    }

    CtClass to = ((TypeName)type).getType();
    Expression e = (Expression)expr;

    if (! isCastable(e.getType(), to, reader.getFilePath(), reader.getLine())) {
      FailLog flog = new FailLog(e.getType().getName() + " cannot cast to " + to.getName(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    return new CastExpression(to, e);
  }

  @Override
  public String toString() {
    return "CastExpressionParser";
  }

  public void init(TypeResolver resolver) {
    this.resolver = resolver;
    super.init();
  }

  public static final CastExpressionParser parser = new CastExpressionParser();

  private CastExpressionParser() {}

  private TypeResolver resolver;
}
