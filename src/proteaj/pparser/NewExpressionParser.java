package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.*;

public class NewExpressionParser extends PackratParser {
  /* NewExpression
   *  : "new" ClassName Arguments
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "new"
    TypedAST newkeyword = KeywordParser.getParser("new").applyRule(reader, env);
    if(newkeyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(newkeyword.getFailLog());
    }

    // ClassName
    TypedAST clsName = ClassNameParser.parser.applyRule(reader, env);
    if(clsName.isFail()) {
      reader.setPos(pos);
      return new BadAST(clsName.getFailLog());
    }

    CtClass type = ((ClassName)clsName).getCtClass();
    int apos = reader.getPos();

    // Arguments
    for(CtConstructor constructor : type.getDeclaredConstructors()) try {
      if(! constructor.visibleFrom(thisClass)) continue;

      TypedAST args = ArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
      if(args.isFail() && hasVarArgs(constructor.getModifiers())) {
        args = VariableArgumentsParser.getParser(constructor.getParameterTypes()).applyRule(reader, env, apos);
      }

      if(! args.isFail()) {
        env.addExceptions(constructor.getExceptionTypes(), reader.getLine());
        return new NewExpression(constructor, (Arguments)args);
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
    }

    // fail
    FailLog flog = new FailLog("undefined constructor", reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtClass thisClass) {
    this.thisClass = thisClass;
  }

  @Override
  public String toString() {
    return "NewExpressionParser";
  }

  public static final NewExpressionParser parser = new NewExpressionParser();

  private NewExpressionParser() {}

  private CtClass thisClass;
}

