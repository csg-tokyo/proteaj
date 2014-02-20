package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

public class BodyCompiler {
  public BodyCompiler(IR ir) {
    this.ir = ir;
    this.parser = new BodyParser();
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
      Environment env = new Environment(ir, method);
      SourceStringReader reader = new SourceStringReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }

      try {
        MethodBody mbody = parser.parseMethodBody(method, reader, env);
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
      Environment env = new Environment(ir, constructor);
      SourceStringReader reader = new SourceStringReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
      }

      try {
        ConstructorBody cbody = parser.parseConstructorBody(constructor, reader, env);
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
      Environment env = new Environment(ir, field);
      SourceStringReader reader = new SourceStringReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        FieldBody fbody = parser.parseFieldBody(field, reader, env);
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
      Environment env = new Environment(ir, method);
      SourceStringReader reader = new SourceStringReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        DefaultValue defval = parser.parseDefaultArgument(method, reader, env);
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
      Environment env = new Environment(ir, clinit);
      SourceStringReader reader = new SourceStringReader(sinit.getSource(), env.filePath, sinit.getLine());

      try {
        ClassInitializer body = parser.parseStaticInitializer(reader, env);
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
