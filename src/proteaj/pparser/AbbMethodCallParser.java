package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;
import static proteaj.util.CtClassUtil.*;

public class AbbMethodCallParser extends PackratParser {
  /* AbbMethodCall
   *  : Identifier Arguments
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // Identifier
    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    String name = ((Identifier)identifier).getName();
    int apos = reader.getPos();

    // Arguments
    for(CtMethod method : getMethods(thisClass)) try {
      if(! method.getName().equals(name)) continue;
      if(isStatic(method.getModifiers()) || ! isStaticMember) {
        TypedAST args = ArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
        if(args.isFail() && hasVarArgs(method.getModifiers())) {
          args = VariableArgumentsParser.getParser(method.getParameterTypes()).applyRule(reader, env, apos);
        }
        if(! args.isFail()) {
          env.addExceptions(method.getExceptionTypes(), reader.getLine());
          if(isStatic(method.getModifiers())) return new StaticMethodCall(method, (Arguments)args);
          else return new MethodCall(new ThisExpression(thisClass), method, (Arguments)args);
        }
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // TODO static import

    // fail
    FailLog flog = new FailLog("undefined method : " + name, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtClass thisClass, CtMember signature) {
    this.thisClass = thisClass;
    this.isStaticMember = isStatic(signature.getModifiers());
  }

  @Override
  public String toString() {
    return "AbbMethodCallParser";
  }

  public static final AbbMethodCallParser parser = new AbbMethodCallParser();

  private boolean isStaticMember;
  private CtClass thisClass;

  private AbbMethodCallParser() {}
}

