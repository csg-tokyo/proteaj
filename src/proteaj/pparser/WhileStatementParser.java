package proteaj.pparser;

import proteaj.ir.Environment;
import proteaj.ir.tast.*;

import javassist.*;

public class WhileStatementParser extends ComposedParser_Sequential {
  /* WhileStatement
   *  : "while" '(' Expression ')' SingleStatement
   */
  private WhileStatementParser() {
    super("WhileStatementParser");
  }

  @Override
  protected PackratParser[] getParsers(Environment env) {
    return new PackratParser[] {
        KeywordParser.getParser("while"),
        KeywordParser.getParser("("),
        ExpressionParser.getParser(CtClass.booleanType, env),
        KeywordParser.getParser(")"),
        SingleStatementParser.parser
    };
  }

  @Override
  protected TypedAST makeAST(int pos, int line, String file, TypedAST... as) {
    return new WhileStatement((Expression)as[2], (Statement)as[4]);
  }

  public static final WhileStatementParser parser = new WhileStatementParser();
}

