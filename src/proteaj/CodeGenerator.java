package proteaj;

import proteaj.codegen.lazy.TranslateLazy;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.tast.*;
import proteaj.codegen.JavaCodeGenerator;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

import javassist.*;

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
    program.getClasses().stream().forEach(clazz -> {
      JavaCodeGenerator gen = new JavaCodeGenerator(clazz.clazz);
      for (CtField field : clazz.getDeclaredFields_Ordered()) {
        FieldDeclaration decl = clazz.getField(field);
        if (decl != null) codegen(decl, gen);
      }
    });
  }

  private void codegen (ClassDeclaration clazz) {
     // generateJavaCode(clazz);
    codegenByJavassist(clazz);
  }

  private void codegenByJavassist (ClassDeclaration clazz) {
    JavaCodeGenerator gen = new JavaCodeGenerator(clazz.clazz);

    for (ClassInitializerDefinition clIni : clazz.getInitializers())
      codegen(clIni, gen);

    for (ConstructorDeclaration constructor : clazz.getConstructors())
      codegen(constructor, gen);

    for (MethodDeclaration method : clazz.getMethods())
      codegen(method, gen);

    for (DefaultValueDefinition d : clazz.getDefaultValues())
      codegen(d, gen);

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
    }
  }

  private void generateJavaCode (ClassDeclaration clazz) {
    try {
      Path dirPath = Paths.get("generated/" + clazz.filePath).getParent();
      if (! Files.exists(dirPath)) dirPath = Files.createDirectories(dirPath);

      Path path = Paths.get(dirPath.toString() + "/" + clazz.clazz.getSimpleName() + ".java");
      if (! Files.exists(path)) path = Files.createFile(path);

      Writer writer = Files.newBufferedWriter(path, Charset.defaultCharset());
      writer.write(JavaCodeGenerator.generateJavaCode(clazz));
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void codegen (OperatorModuleDeclaration syntax) {
    try {
      if(target != null) new OperatorsFile(syntax.syntax).write(target);
      else new OperatorsFile(syntax.syntax).write();
    } catch (CompileError e) {
      ErrorList.addError(e);
    }
  }

  private void codegen (MethodDeclaration method, JavaCodeGenerator gen) {
    try { method.method.setBody(gen.codeGen(method.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ConstructorDeclaration constructor, JavaCodeGenerator gen) {
    try { constructor.constructor.setBody(gen.codeGen(constructor.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (FieldDeclaration field, JavaCodeGenerator gen) {
    try {
      CtClass thisClass = field.field.getDeclaringClass();
      thisClass.removeField(field.field);
      thisClass.addField(field.field, gen.codeGen(field.body));
    } catch (NotFoundException | CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (DefaultValueDefinition defaultValue, JavaCodeGenerator gen) {
    try { defaultValue.method.setBody(gen.codeGen(defaultValue.body)); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ClassInitializerDefinition clIni, JavaCodeGenerator gen) {
    try {
      clIni.clIni.insertAfter(gen.codeGen(clIni.body));
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private final String target;
}

