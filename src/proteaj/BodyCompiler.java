package proteaj;

import proteaj.error.*;
import proteaj.ir.*;
import proteaj.pparser.Environment;
import proteaj.pparser.PackratReader;
import proteaj.tast.*;

import java.util.*;
import java.util.stream.Collectors;

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
    return ir.getMethods().stream().map(method -> {
      Environment env = new Environment(ir, method.ctMethod);
      PackratReader reader = new PackratReader(method.source, env.filePath, method.line);

      try { env.addParams(method.paramNames, method.getParamTypes()); } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, env.filePath, reader.getLine()));
      }

      try {
        return new MethodDeclaration(method.ctMethod, parser.parseMethodBody(method.ctMethod, reader, env));
      } catch (CompileErrors e) {
        ErrorList.addErrors(e);
        return null;
      }
    }).filter(a -> a != null).collect(Collectors.toList());
  }

  private List<ConstructorDeclaration> compileConstructors() {
    return ir.getConstructors().stream().map(constructor -> {
      Environment env = new Environment(ir, constructor.ctConstructor);
      PackratReader reader = new PackratReader(constructor.source, env.filePath, constructor.line);

      try { env.addParams(constructor.paramNames, constructor.getParamTypes()); } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, env.filePath, reader.getLine()));
      }

      try {
        return new ConstructorDeclaration(constructor.ctConstructor, parser.parseConstructorBody(constructor.ctConstructor, reader, env));
      } catch (CompileErrors e) {
        ErrorList.addErrors(e);
        return null;
      }
    }).filter(a -> a != null).collect(Collectors.toList());
  }

  private List<FieldDeclaration> compileFields() {
    return ir.getFields().stream().map(field -> {
      Environment env = new Environment(ir, field.ctField);
      PackratReader reader = new PackratReader(field.source, env.filePath, field.line);

      try {
        return new FieldDeclaration(field.ctField, parser.parseFieldBody(field.ctField, reader, env));
      } catch (CompileErrors e) {
        ErrorList.addErrors(e);
        return null;
      }
    }).filter(a -> a != null).collect(Collectors.toList());
  }

  private List<DefaultValueDefinition> compileDefaultArguments() {
    return ir.getDefaultArguments().stream().map(arg -> {
      Environment env = new Environment(ir, arg.ctMethod);
      PackratReader reader = new PackratReader(arg.source, env.filePath, arg.line);

      try {
        return new DefaultValueDefinition(arg.ctMethod, parser.parseDefaultArgument(arg.ctMethod, reader, env));
      } catch (CompileErrors e) {
        ErrorList.addErrors(e);
        return null;
      }
    }).filter(a -> a != null).collect(Collectors.toList());
  }

  private List<ClassInitializerDefinition> compileStaticInitializers() {
    return ir.getStaticInitializers().stream().map(sInit -> {
      Environment env = new Environment(ir, sInit.clInit);
      PackratReader reader = new PackratReader(sInit.source, env.filePath, sInit.line);

      try {
        return new ClassInitializerDefinition(sInit.clInit, parser.parseStaticInitializer(reader, env));
      } catch (CompileErrors e) {
        ErrorList.addErrors(e);
        return null;
      }
    }).filter(a -> a != null).collect(Collectors.toList());
  }

  private IR ir;
  private BodyParser parser;
}
