package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class PrimaryParser extends PackratParser {
  /* Primary
   *  : AbbMethodCall
   *  | Variable
   *  | StaticMethodCall
   *  | StaticFieldAccess
   *  | NewExpression
   *  | NewArrayExpression
   *  | Literal
   *  | ParenthesizedJavaExpression
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST abbmcall = AbbMethodCallParser.parser.applyRule(reader, env, pos);
    if(! abbmcall.isFail()) return abbmcall;

    TypedAST variable = VariableParser.parser.applyRule(reader, env, pos);
    if(! variable.isFail()) return variable;

    TypedAST stmcall = StaticMethodCallParser.parser.applyRule(reader, env, pos);
    if(! stmcall.isFail()) return stmcall;

    TypedAST sfaccess = StaticFieldAccessParser.parser.applyRule(reader, env, pos);
    if(! sfaccess.isFail()) return sfaccess;

    TypedAST newexpr = NewExpressionParser.parser.applyRule(reader, env, pos);
    if(! newexpr.isFail()) return newexpr;

    TypedAST newarray = NewArrayExpressionParser.parser.applyRule(reader, env, pos);
    if(! newarray.isFail()) return newarray;

    TypedAST parenexp = ParenthesizedJavaExpressionParser.parser.applyRule(reader, env, pos);
    if(! parenexp.isFail()) return parenexp;

    TypedAST literal = LiteralParser.parser.applyRule(reader, env, pos);
    if(! literal.isFail()) return literal;

    // fail
    FailLog flog = chooseBest(abbmcall.getFailLog(), variable.getFailLog(), stmcall.getFailLog(),
        sfaccess.getFailLog(), newexpr.getFailLog(), newarray.getFailLog(),
        parenexp.getFailLog(), literal.getFailLog());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return "PrimaryParser";
  }

  public static final PrimaryParser parser = new PrimaryParser();

  private PrimaryParser() {}
}

