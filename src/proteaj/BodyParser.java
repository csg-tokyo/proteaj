package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.pparser.*;

import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class BodyParser {
  public MethodBody parseMethodBody(CtMethod method, SourceStringReader reader, Environment env) throws CompileErrors {
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

      throw new CompileErrors(new ParseError(mbody.getFailLog().getMessage(), reader.filePath, mbody.getFailLog().getLine()));
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public ConstructorBody parseConstructorBody(CtConstructor constructor, SourceStringReader reader, Environment env) throws CompileErrors {
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

      throw new CompileErrors(new ParseError(cbody.getFailLog().getMessage(), reader.filePath, cbody.getFailLog().getLine()));
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public FieldBody parseFieldBody(CtField field, SourceStringReader reader, Environment env) throws CompileErrors {
    try {
      ParseResult<FieldBody> fbody = FieldBodyParser.getParser(field.getType()).applyRule(reader, env);
      if(! fbody.isFail()) {
        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }
        return fbody.get();
      }

      throw new CompileErrors(new ParseError(fbody.getFailLog().getMessage(), reader.filePath, fbody.getFailLog().getLine()));
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public DefaultValue parseDefaultArgument(CtMethod method, SourceStringReader reader, Environment env) throws CompileErrors {
    try {
      ParseResult<DefaultValue> defval = DefaultArgumentParser.getParser(method.getReturnType()).applyRule(reader, env);
      if(! defval.isFail()) {
        if(env.hasException()) {
          throw createUnhandledExceptions(reader, env);
        }
        return defval.get();
      }

      throw new CompileErrors(new ParseError(defval.getFailLog().getMessage(), reader.filePath, defval.getFailLog().getLine()));
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public ClassInitializer parseStaticInitializer(SourceStringReader reader, Environment env) throws CompileErrors {
    initParser_Statement();

    ParseResult<ClassInitializer> sibody = StaticInitializerParser.parser.applyRule(reader, env);
    if(! sibody.isFail()) {
      if(env.hasException()) {
        throw createUnhandledExceptions(reader, env);
      }

      return sibody.get();
    }

    throw new CompileErrors(new ParseError(sibody.getFailLog().getMessage(), reader.filePath, sibody.getFailLog().getLine()));
  }

  private CompileErrors createUnhandledExceptions(SourceStringReader reader, Environment env) {
    List<CompileError> errors = new ArrayList<CompileError>();
    for(Entry<CtClass, List<Integer>> entry : env.getExceptionsData().entrySet()) {
      CtClass exception = entry.getKey();
      for(int line : entry.getValue()) {
        errors.add(new ParseError("unhandled exception type " + exception.getName(), reader.filePath, line));
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
