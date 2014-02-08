package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;
import static proteaj.util.CtClassUtil.*;

public class MethodCallParser extends PackratParser {
  /* MethodCall
   *  : JavaExpression '.' Identifier Arguments
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

    Expression expr = (Expression)jexpr;

    // '.'
    TypedAST dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) {
      reader.setPos(pos);
      return new BadAST(dot.getFailLog());
    }

    // Identifier
    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    String name = ((Identifier)identifier).getName();
    int apos = reader.getPos();

    // Arguments
    for(CtMethod method : getMethods(expr.getType())) try {
      if(isStatic(method.getModifiers()) || ! method.visibleFrom(thisClass) || ! method.getName().equals(name)) continue;

      TypedAST args = ArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(method.getModifiers())) {
        args = VariableArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(! args.isFail()) {
        env.addExceptions(method.getExceptionTypes(), reader.getLine());
        return new MethodCall(expr, method, (Arguments)args);
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    FailLog flog = new FailLog("undefined method : " + name, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtClass thisClass) {
    this.thisClass = thisClass;
    super.init();
  }

  @Override
  public String toString() {
    return "MethodCallParser";
  }

  public static final MethodCallParser parser = new MethodCallParser();

  private CtClass thisClass;

  private MethodCallParser() {}
}

