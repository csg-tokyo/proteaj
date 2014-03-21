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

      ParseResult<MethodBody> mbody = StatementParsers.methodBody(returnType).applyRule(reader, env);
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

      ParseResult<ConstructorBody> cbody = StatementParsers.constructorBody().applyRule(reader, env);
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
      ParseResult<FieldBody> fbody = ExpressionParsers.fieldBody(field.getType()).applyRule(reader, env);
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
      ParseResult<DefaultValue> defval = ExpressionParsers.defaultArgument(method.getReturnType()).applyRule(reader, env);
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
    ParseResult<ClassInitializer> sibody = StatementParsers.classInitializer().applyRule(reader, env);
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
    for(Entry<CtClass, List<Integer>> entry : env.getExceptions().entrySet()) {
      CtClass exception = entry.getKey();
      for(int line : entry.getValue()) {
        errors.add(new ParseError("unhandled exception type " + exception.getName(), reader.filePath, line));
      }
    }
    return new CompileErrors(errors);
  }
}
