package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

public class JavaExpressionParser extends PackratParser {
  /* JavaExpression
   *  : AssignExpression
   *  | ArrayLength
   *  | MethodCall
   *  | FieldAccess
   *  | ArrayAccess
   *  | Primary
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST assign = AssignExpressionParser.parser.applyRule(reader, env, pos);
    if(! assign.isFail()) return assign;

    TypedAST alength = ArrayLengthParser.parser.applyRule(reader, env, pos);
    if(! alength.isFail()) return alength;

    TypedAST mcall = MethodCallParser.parser.applyRule(reader, env, pos);
    if(! mcall.isFail()) return mcall;

    TypedAST faccess = FieldAccessParser.parser.applyRule(reader, env, pos);
    if(! faccess.isFail()) return faccess;

    TypedAST aaccess = ArrayAccessParser.parser.applyRule(reader, env, pos);
    if(! aaccess.isFail()) return aaccess;

    TypedAST primary = PrimaryParser.parser.applyRule(reader, env, pos);
    if(! primary.isFail()) return primary;

    // fail
    FailLog flog = chooseBest(assign.getFailLog(), alength.getFailLog(), mcall.getFailLog(), faccess.getFailLog(), aaccess.getFailLog(), primary.getFailLog());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return "JavaExpressionParser";
  }

  public static final JavaExpressionParser parser = new JavaExpressionParser();

  private JavaExpressionParser() {}
}

