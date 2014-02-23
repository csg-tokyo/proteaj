package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;
import proteaj.pparser.*;

import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class BodyParser {
  public MethodBody parseMethodBody(CtMethod method, SourceStringReader reader, Environment env) throws CompileError, CompileErrors {
    try {
      CtClass returnType = method.getReturnType();
      CtClass[] exceptionTypes = method.getExceptionTypes();
      initParser_Statement(returnType);

      ParseResult<MethodBody> mbody = MethodBodyParser.parser.applyRule(reader, env);
      if(! mbody.isFail()) {
        env.removeExceptions(exceptionTypes);

        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }

        return mbody.get();
      }

      throw new ParseError(mbody.getFailLog().getMessage(), reader.getFilePath(), mbody.getFailLog().getLine());
    } catch (NotFoundException e) {
      throw new NotFoundError(e, reader.getFilePath(), 0);
    }
  }

  public ConstructorBody parseConstructorBody(CtConstructor constructor, SourceStringReader reader, Environment env) throws CompileError, CompileErrors {
    try {
      CtClass[] exceptionTypes = constructor.getExceptionTypes();
      initParser_Statement();

      ParseResult<ConstructorBody> cbody = ConstructorBodyParser.parser.applyRule(reader, env);
      if(! cbody.isFail()) {
        env.removeExceptions(exceptionTypes);

        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }

        return cbody.get();
      }

      throw new ParseError(cbody.getFailLog().getMessage(), reader.getFilePath(), cbody.getFailLog().getLine());
    } catch (NotFoundException e) {
      throw new NotFoundError(e, reader.getFilePath(), 0);
    }
  }

  public FieldBody parseFieldBody(CtField field, SourceStringReader reader, Environment env) throws CompileError, CompileErrors {
    try {
      ParseResult<FieldBody> fbody = FieldBodyParser.getParser(field.getType()).applyRule(reader, env);
      if(! fbody.isFail()) {
        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }
        return fbody.get();
      }

      throw new ParseError(fbody.getFailLog().getMessage(), reader.getFilePath(), fbody.getFailLog().getLine());
    } catch (NotFoundException e) {
      throw new NotFoundError(e, reader.getFilePath(), 0);
    }
  }

  public DefaultValue parseDefaultArgument(CtMethod method, SourceStringReader reader, Environment env) throws CompileError, CompileErrors {
    try {
      ParseResult<DefaultValue> defval = DefaultArgumentParser.getParser(method.getReturnType()).applyRule(reader, env);
      if(! defval.isFail()) {
        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }
        return defval.get();
      }

      throw new ParseError(defval.getFailLog().getMessage(), reader.getFilePath(), defval.getFailLog().getLine());
    } catch (NotFoundException e) {
      throw new NotFoundError(e, reader.getFilePath(), 0);
    }
  }

  public ClassInitializer parseStaticInitializer(SourceStringReader reader, Environment env) throws CompileError, CompileErrors {
    initParser_Statement();

    ParseResult<ClassInitializer> sibody = StaticInitializerParser.parser.applyRule(reader, env);
    if(! sibody.isFail()) {
      if(env.hasException()) {
        throw createUnhandledExceptions(reader, env);
      }

      return sibody.get();
    }

    throw new ParseError(sibody.getFailLog().getMessage(), reader.getFilePath(), sibody.getFailLog().getLine());
  }

  private CompileErrors createUnhandledExceptions(SourceStringReader reader, Environment env) {
    List<CompileError> errors = new ArrayList<CompileError>();
    for(Entry<CtClass, List<Integer>> entry : env.getExceptionsData().entrySet()) {
      CtClass exception = entry.getKey();
      for(int line : entry.getValue()) {
        errors.add(new ParseError("unhandled exception type " + exception.getName(), reader.getFilePath(), line));
      }
    }
    return new CompileErrors(errors);
  }

  private void initParser_Statement() {
    ReturnStatementParser.parser.disable();
  }

  private void initParser_Statement(CtClass returnType) {
    ReturnStatementParser.parser.init(returnType);
  }
}
