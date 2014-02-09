package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.isStatic;

public class BodyCompiler {
  public BodyCompiler(IR ir) {
    this.ir = ir;
    this.parser = new BodyParser(ir);
  }

  public IR compile() {
    compileConstructors();
    compileMethods();
    compileFields();
    compileDefaultArguments();
    compileStaticInitializers();

    return ir;
  }

  private void compileMethods() {
    for(IRMethodBody irbody : ir.getMethods()) {
      CtMethod method = irbody.getCtMethod();
      CtClass thisClass = method.getDeclaringClass();
      IRHeader header = ir.getIRHeader(thisClass);

      SourceStringReader reader = new SourceStringReader(irbody.getSource(), header.getFilePath(), irbody.getLine());
      TypeResolver resolver = new TypeResolver(header, ir.getClassPool());
      UsingOperators usops = new UsingOperators(header, ir.getOperatorPool());
      Environment env = new Environment(thisClass, isStatic(method.getModifiers()), header.getFilePath());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }

      try {
        MethodBody mbody = parser.parseMethodBody(method, reader, env, resolver, usops);
        irbody.setAST(mbody);
      } catch (CompileError e) {
        ErrorList.addError(e);
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }
  }

  private void compileConstructors() {
    for(IRConstructorBody irbody : ir.getConstructors()) {
      CtConstructor constructor = irbody.getCtConstructor();
      CtClass thisClass = constructor.getDeclaringClass();
      IRHeader header = ir.getIRHeader(thisClass);

      SourceStringReader reader = new SourceStringReader(irbody.getSource(), header.getFilePath(), irbody.getLine());
      TypeResolver resolver = new TypeResolver(header, ir.getClassPool());
      UsingOperators usops = new UsingOperators(header, ir.getOperatorPool());
      Environment env = new Environment(thisClass, isStatic(constructor.getModifiers()), header.getFilePath());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }

      try {
        ConstructorBody cbody = parser.parseConstructorBody(constructor, reader, env, resolver, usops);
        irbody.setAST(cbody);
      } catch (CompileError e) {
        ErrorList.addError(e);
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }
  }

  private void compileFields() {
    for(IRFieldBody irbody : ir.getFields()) {
      CtField field = irbody.getCtField();
      CtClass thisClass = field.getDeclaringClass();
      IRHeader header = ir.getIRHeader(thisClass);

      SourceStringReader reader = new SourceStringReader(irbody.getSource(), header.getFilePath(), irbody.getLine());
      TypeResolver resolver = new TypeResolver(header, ir.getClassPool());
      UsingOperators usops = new UsingOperators(header, ir.getOperatorPool());
      Environment env = new Environment(thisClass, isStatic(field.getModifiers()), header.getFilePath());

      try {
        FieldBody fbody = parser.parseFieldBody(field, reader, env, resolver, usops);
        irbody.setAST(fbody);
      } catch (CompileError e) {
        ErrorList.addError(e);
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }
  }

  private void compileDefaultArguments() {
    for(IRDefaultArgument irbody : ir.getDefaultArguments()) {
      CtMethod method = irbody.getCtMethod();
      CtClass thisClass = method.getDeclaringClass();
      IRHeader header = ir.getIRHeader(thisClass);

      SourceStringReader reader = new SourceStringReader(irbody.getSource(), header.getFilePath(), irbody.getLine());
      TypeResolver resolver = new TypeResolver(header, ir.getClassPool());
      UsingOperators usops = new UsingOperators(header, ir.getOperatorPool());
      Environment env = new Environment(thisClass, isStatic(method.getModifiers()), header.getFilePath());

      try {
        DefaultValue defval = parser.parseDefaultArgument(method, reader, env, resolver, usops);
        irbody.setAST(defval);
      } catch (CompileError e) {
        ErrorList.addError(e);
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }
  }

  private void compileStaticInitializers() {
    for(IRStaticInitializer sinit : ir.getStaticInitializers()) {
      CtConstructor clinit = sinit.getCtConstructor();
      CtClass thisClass = clinit.getDeclaringClass();
      IRHeader header = ir.getIRHeader(thisClass);

      SourceStringReader reader = new SourceStringReader(sinit.getSource(), header.getFilePath(), sinit.getLine());
      TypeResolver resolver = new TypeResolver(header, ir.getClassPool());
      UsingOperators usops = new UsingOperators(header, ir.getOperatorPool());
      Environment env = new Environment(thisClass, isStatic(clinit.getModifiers()), header.getFilePath());

      try {
        ClassInitializer body = parser.parseStaticInitializer(clinit, reader, env, resolver, usops);
        sinit.setAST(body);
      } catch (CompileError e) {
        ErrorList.addError(e);
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }
  }

  private IR ir;
  private BodyParser parser;
}
