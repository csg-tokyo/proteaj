package proteaj;

import proteaj.error.*;
import proteaj.tast.*;
import proteaj.pparser.*;

import java.util.*;
import java.util.Map.Entry;

import javassist.*;

public class BodyParser {
  public MethodBody parseMethodBody(CtMethod method, PackratReader reader, Environment env) throws CompileErrors {
    ForDebug.print("[[ parse body of " + method.getName() + " ]]");
    try {
      CtClass returnType = method.getReturnType();
      CtClass[] exceptionTypes = method.getExceptionTypes();

      ParseResult<MethodBody> mbody = StatementParsers.methodBody(returnType).applyRule(reader, env);
      env.removeExceptions(exceptionTypes);
      return getResultOrThrowErrors(mbody, reader, env);
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public ConstructorBody parseConstructorBody(CtConstructor constructor, PackratReader reader, Environment env) throws CompileErrors {
    ForDebug.print("[[ parse body of constructor " + constructor.getSignature() + " ]]");
    try {
      CtClass[] exceptionTypes = constructor.getExceptionTypes();

      ParseResult<ConstructorBody> cbody = StatementParsers.constructorBody().applyRule(reader, env);
      env.removeExceptions(exceptionTypes);
      return getResultOrThrowErrors(cbody, reader, env);
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public FieldBody parseFieldBody(CtField field, PackratReader reader, Environment env) throws CompileErrors {
    ForDebug.print("[[ parse field initializer of " + field.getName() + " ]]");
    try {
      ParseResult<FieldBody> fbody = ExpressionParsers.fieldBody(field.getType()).applyRule(reader, env);
      return getResultOrThrowErrors(fbody, reader, env);
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public DefaultValue parseDefaultArgument(CtMethod method, PackratReader reader, Environment env) throws CompileErrors {
    try {
      ParseResult<DefaultValue> defval = ExpressionParsers.defaultArgument(method.getReturnType()).applyRule(reader, env);
      return getResultOrThrowErrors(defval, reader, env);
    } catch (NotFoundException e) {
      throw new CompileErrors(new NotFoundError(e, reader.filePath, 0));
    }
  }

  public ClassInitializer parseStaticInitializer(PackratReader reader, Environment env) throws CompileErrors {
    ParseResult<ClassInitializer> sibody = StatementParsers.classInitializer().applyRule(reader, env);
    return getResultOrThrowErrors(sibody, reader, env);
  }

  private <T> T getResultOrThrowErrors (ParseResult<T> result, PackratReader reader, Environment env) throws CompileErrors {
    if (result.isFail()) {
      Optional<Failure<?>> failure = reader.getAllFailures().max((f1, f2) -> f1.pos - f2.pos);

      if (failure.isPresent())
        throw new CompileErrors(new ParseError(failure.get().msg, env.filePath, failure.get().line));
      else
        throw new CompileErrors(new ParseError(result.getFailLog().getMessage(), env.filePath, result.getFailLog().getLine()));
    }
    if (env.hasException()) {
      warnUnhandledExceptions(env);
    }
    return result.get();
  }

  private void warnUnhandledExceptions(Environment env) {
    for(Entry<CtClass, List<Integer>> entry : env.getExceptions().entrySet()) {
      CtClass exception = entry.getKey();
      for(int line : entry.getValue()) {
        Warning.print("unhandled exception type " + exception.getName(), env.filePath, line);
      }
    }
  }
}
