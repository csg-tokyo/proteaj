package proteaj;

import proteaj.codegen.lazy.TranslateLazy;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.tast.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

import javassist.*;

import static proteaj.codegen.JavaCodeGenerator.generateJavaCode;
import static proteaj.codegen.JavassistCodeGenerator.codeGen;

public class CodeGenerator {
  public CodeGenerator() {
    this.target = null;
  }

  public CodeGenerator(String target) {
    this.target = target;
  }

  public void codegen (Program program) {
    TranslateLazy translator = new TranslateLazy(program);
    program = translator.translate();

    codegenFields(program);

    for (ClassDeclaration clazz : program.getClasses()) codegen(clazz);
    for (OperatorModuleDeclaration syntax : program.getOperatorsModules()) codegen(syntax);
  }

  private void codegenFields (Program program) {
    program.getClasses().stream().forEach(clazz -> clazz.getFields().stream().forEach(this::codegen));
  }

  private void codegen (ClassDeclaration clazz) {
    try {
      Path dirPath = Paths.get("generated/" + clazz.filePath).getParent();
      if (! Files.exists(dirPath)) dirPath = Files.createDirectories(dirPath);

      Path path = Paths.get(dirPath.toString() + "/" + clazz.clazz.getSimpleName() + ".java");
      if (! Files.exists(path)) path = Files.createFile(path);

      System.out.println("write " + path.toString());

      Writer writer = Files.newBufferedWriter(path, Charset.defaultCharset());
      writer.write(generateJavaCode(clazz));
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
/*
    for (ClassInitializerDefinition clIni : clazz.getInitializers())
      codegen(clIni);

    for (ConstructorDeclaration constructor : clazz.getConstructors())
      codegen(constructor);

    for (MethodDeclaration method : clazz.getMethods())
      codegen(method);

    for (DefaultValueDefinition d : clazz.getDefaultValues())
      codegen(d);

    try {
      if (target != null) clazz.clazz.writeFile(target);
      else clazz.clazz.writeFile();
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, clazz.filePath, 0));
    } catch (IOException e) {
      ErrorList.addError(new FileIOError("can't write class file", clazz.filePath, 0));
    }*/
  }

  private void codegen (OperatorModuleDeclaration syntax) {
    try {
      if(target != null) new OperatorsFile(syntax.syntax).write(target);
      else new OperatorsFile(syntax.syntax).write();
    } catch (CompileError e) {
      ErrorList.addError(e);
    }
  }

  private void codegen (MethodDeclaration method) {
    try { method.method.setBody(codeGen(method.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ConstructorDeclaration constructor) {
    try { constructor.constructor.setBody(codeGen(constructor.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (FieldDeclaration field) {
    try {
      CtClass thisClass = field.field.getDeclaringClass();
      thisClass.removeField(field.field);
      thisClass.addField(field.field, codeGen(field.body));
    } catch (NotFoundException | CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (DefaultValueDefinition defaultValue) {
    try { defaultValue.method.setBody(codeGen(defaultValue.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ClassInitializerDefinition clIni) {
    try {
      clIni.clIni.insertAfter(codeGen(clIni.body));
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private final String target;
}

