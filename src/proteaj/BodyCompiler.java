package proteaj;

import proteaj.error.*;
import proteaj.ir.*;
import proteaj.pparser.Environment;
import proteaj.pparser.PackratReader;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class BodyCompiler {
  public BodyCompiler(IR ir) {
    this.ir = ir;
    this.parser = new BodyParser();
  }

  public Program compile() {
    Program program = new Program(ir);

    program.addConstructors(compileConstructors());
    program.addMethods(compileMethods());
    program.addFields(compileFields());
    program.addDefaultValues(compileDefaultArguments());
    program.addClassInitializers(compileStaticInitializers());

    return program;
  }

  private List<MethodDeclaration> compileMethods() {
    List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

    for(IRMethodBody irbody : ir.getMethods()) {
      CtMethod method = irbody.getCtMethod();
      Environment env = new Environment(ir, method);
      PackratReader reader = new PackratReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, env.filePath, reader.getLine()));
      }

      try {
        MethodBody body = parser.parseMethodBody(method, reader, env);
        methods.add(new MethodDeclaration(method, body));
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }

    return methods;
  }

  private List<ConstructorDeclaration> compileConstructors() {
    List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();

    for(IRConstructorBody irbody : ir.getConstructors()) {
      CtConstructor constructor = irbody.getCtConstructor();
      Environment env = new Environment(ir, constructor);
      PackratReader reader = new PackratReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        env.addParams(irbody.getParamNames(), irbody.getParamTypes());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }

      try {
        ConstructorBody body = parser.parseConstructorBody(constructor, reader, env);
        constructors.add(new ConstructorDeclaration(constructor, body));
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }

    return constructors;
  }

  private List<FieldDeclaration> compileFields() {
    List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();

    for(IRFieldBody irbody : ir.getFields()) {
      CtField field = irbody.getCtField();
      Environment env = new Environment(ir, field);
      PackratReader reader = new PackratReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        FieldBody body = parser.parseFieldBody(field, reader, env);
        //irbody.setAST(fbody);
        fields.add(new FieldDeclaration(field, body));
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }

    return fields;
  }

  private List<DefaultValueDefinition> compileDefaultArguments() {
    List<DefaultValueDefinition> map = new ArrayList<DefaultValueDefinition>();

    for(IRDefaultArgument irbody : ir.getDefaultArguments()) {
      CtMethod method = irbody.getCtMethod();
      Environment env = new Environment(ir, method);
      PackratReader reader = new PackratReader(irbody.getSource(), env.filePath, irbody.getLine());

      try {
        DefaultValue defval = parser.parseDefaultArgument(method, reader, env);
        map.add(new DefaultValueDefinition(method, defval));
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }

    return map;
  }

  private List<ClassInitializerDefinition> compileStaticInitializers() {
    List<ClassInitializerDefinition> list = new ArrayList<ClassInitializerDefinition>();

    for(IRStaticInitializer sinit : ir.getStaticInitializers()) {
      CtConstructor clinit = sinit.getCtConstructor();
      Environment env = new Environment(ir, clinit);
      PackratReader reader = new PackratReader(sinit.getSource(), env.filePath, sinit.getLine());

      try {
        ClassInitializer body = parser.parseStaticInitializer(reader, env);
        list.add(new ClassInitializerDefinition(clinit, body));
      } catch (CompileErrors es) {
        for(CompileError e : es.getErrors()) ErrorList.addError(e);
      }
    }

    return list;
  }

  private IR ir;
  private BodyParser parser;
}
