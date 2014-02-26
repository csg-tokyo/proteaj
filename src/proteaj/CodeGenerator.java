package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.tast.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class CodeGenerator {
  public CodeGenerator() {
    this.target = null;
  }

  public CodeGenerator(String target) {
    this.target = target;
  }

  public void codegen (Program program) {
    for (ClassDeclaration clazz : program.getClasses()) codegen(clazz);
    for (OperatorModuleDeclaration syntax : program.getOperatorsModules()) codegen(syntax);
  }

  private void codegen (ClassDeclaration clazz) {
    for (Entry<CtMethod, MethodBody> entry : clazz.getMethods().entrySet())
      codegen(entry.getKey(), entry.getValue());

    for (Entry<CtConstructor, ConstructorBody> entry : clazz.getConstructors().entrySet())
      codegen(entry.getKey(), entry.getValue());

    for (Entry<CtField, FieldBody> entry : clazz.getFields().entrySet())
      codegen(entry.getKey(), entry.getValue());

    for (Entry<CtMethod, DefaultValue> entry : clazz.getDefaultValues().entrySet())
      codegen(entry.getKey(), entry.getValue());

    for (Entry<CtConstructor, List<ClassInitializer>> entry : clazz.getInitializers().entrySet())
      codegen(entry.getKey(), entry.getValue());

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

  private void codegen (OperatorModuleDeclaration syntax) {
    try {
      if(target != null) new OperatorsFile(syntax.syntax).write(target);
      else new OperatorsFile(syntax.syntax).write();
    } catch (CompileError e) {
      ErrorList.addError(e);
    }
  }

  private void codegen (CtMethod method, MethodBody body) {
    try { method.setBody(body.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (CtConstructor constructor, ConstructorBody body) {
    try { constructor.setBody(body.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (CtField field, FieldBody body) {
    try {
      CtClass thisClass = field.getDeclaringClass();
      thisClass.removeField(field);
      thisClass.addField(field, body.toJavassistCode());
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (CtMethod method, DefaultValue defaultValue) {
    try { method.setBody(defaultValue.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (CtConstructor clIni, List<ClassInitializer> bodies) {
    for (ClassInitializer body : bodies) try {
      clIni.setBody(body.toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private String target;
}

