package proteaj.ir;

import proteaj.error.NotFoundError;
import proteaj.error.SemanticsError;
import proteaj.env.type.TypeResolver;

import javassist.*;

public class IRClass {
  public IRClass(CtClass clazz, IRHeader header) {
    this.clazz = clazz;
    this.header = header;
    this.resolver = TypeResolver.onModule(header.resolver);
  }

  public void setSuperclass (CtClass sup) throws SemanticsError {
    try { clazz.setSuperclass(sup); } catch (CannotCompileException e) {
      throw new SemanticsError(e, header.filePath);
    }
  }

  public void addInterface (CtClass it) {
    clazz.addInterface(it);
  }

  public IRStaticInitializer makeStaticInitializer (String source, int line) throws SemanticsError {
    try { return new IRStaticInitializer(clazz.makeClassInitializer(), source, line); } catch (CannotCompileException e) {
      throw new SemanticsError(e, header.filePath);
    }
  }

  public void addDefaultConstructor () {
    assert clazz.getDeclaredConstructors().length == 0;
    try { clazz.addConstructor(CtNewConstructor.defaultConstructor(clazz)); } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    }
  }

  public void addAbstractConstructor (int mod, CtClass[] paramTypes, CtClass[] exceptions) throws NotFoundError, SemanticsError {
    makeCtConstructor(mod, paramTypes, exceptions);
  }

  public IRConstructor makeConstructor (int mod, CtClass[] paramTypes, String[] paramNames, CtClass[] exceptions, String source, int line) throws NotFoundError, SemanticsError {
    return new IRConstructor(makeCtConstructor(mod, paramTypes, exceptions), paramNames, source, line);
  }

  public void addAbstractMethod (int mod, CtClass returnType, String name, CtClass[] paramTypes, CtClass[] exceptions) throws NotFoundError, SemanticsError {
    makeCtMethod(mod, returnType, name, paramTypes, exceptions);
  }

  public IRMethod makeMethod (int mod, CtClass returnType, String name, CtClass[] paramTypes, String[] paramNames, CtClass[] exceptions, String source, int line) throws NotFoundError, SemanticsError {
    return new IRMethod(makeCtMethod(mod, returnType, name, paramTypes, exceptions), paramNames, source, line);
  }

  public void addAbstractField (int mod, CtClass type, String name) throws SemanticsError {
    makeCtField(mod, type, name);
  }

  public IRField makeField (int mod, CtClass type, String name, String source, int line) throws SemanticsError {
    return new IRField(makeCtField(mod, type, name), source, line);
  }

  public String makeUniqueName (String prefix) {
    return clazz.makeUniqueName(prefix);
  }

  public final CtClass clazz;
  public final IRHeader header;
  public final TypeResolver resolver;


  private CtConstructor makeCtConstructor (int mod, CtClass[] paramTypes, CtClass[] exceptions) throws NotFoundError, SemanticsError {
    CtConstructor constructor = new CtConstructor(paramTypes, clazz);
    constructor.setModifiers(mod);

    if (exceptions.length != 0) try {
      constructor.setExceptionTypes(exceptions);
    } catch (NotFoundException e) {
      throw new NotFoundError(e, header.filePath);
    }

    try { clazz.addConstructor(constructor); } catch (CannotCompileException e) {
      throw new SemanticsError(e, header.filePath);
    }

    return constructor;
  }

  // use internal only
  public CtMethod makeCtMethod (int mod, CtClass returnType, String name, CtClass[] paramTypes, CtClass[] exceptions) throws NotFoundError, SemanticsError {
    CtMethod method = new CtMethod(returnType, name, paramTypes, clazz);
    method.setModifiers(mod);

    if (exceptions.length != 0) try {
      method.setExceptionTypes(exceptions);
    } catch (NotFoundException e) {
      throw new NotFoundError(e, header.filePath);
    }

    try { clazz.addMethod(method); } catch (CannotCompileException e) {
      throw new SemanticsError(e, header.filePath);
    }

    return method;
  }

  private CtField makeCtField (int mod, CtClass type, String name) throws SemanticsError {
    final CtField field;
    try {
      field = new CtField(type, name, clazz);
      field.setModifiers(mod);
      clazz.addField(field);
    } catch (CannotCompileException e) {
      throw new SemanticsError(e, header.filePath);
    }
    return field;
  }
}
