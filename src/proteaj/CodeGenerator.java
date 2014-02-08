package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;

import java.io.*;
import javassist.*;

public class CodeGenerator {
  public CodeGenerator() {
    this.target = null;
  }

  public CodeGenerator(String target) {
    this.target = target;
  }

  public void codegen(IR ir) {
    codegenMethods(ir);
    codegenConstructors(ir);
    codegenFields(ir);
    codegenDefaultArgs(ir);
    codegenStaticInitializer(ir);
    codegenClasses(ir);
    codegenSyntax(ir);
  }

  private void codegenClasses(IR ir) {
    for(CtClass clz : ir.getClasses()) try {
      if(target != null) clz.writeFile(target);
      else clz.writeFile();
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, ir.getIRHeader(clz).getFilePath(), 0));
    } catch (IOException e) {
      ErrorList.addError(new FileIOError("can't write class file", ir.getIRHeader(clz).getFilePath(), 0));
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegenSyntax(IR ir) {
    for(IRSyntax syn : ir.getSyntax()) try {
      if(target != null) new OperatorsFile(syn).write(target);
      else new OperatorsFile(syn).write();
    } catch (CompileError e) {
      ErrorList.addError(e);
    }
  }

  private void codegenMethods(IR ir) {
    for(IRMethodBody irbody : ir.getMethods()) try {
      if(irbody.hasAST()) irbody.getCtMethod().setBody(irbody.getAST().toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegenConstructors(IR ir) {
    for(IRConstructorBody irbody : ir.getConstructors()) try {
      if(irbody.hasAST()) irbody.getCtConstructor().setBody(irbody.getAST().toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegenFields(IR ir) {
    for(IRFieldBody irbody : ir.getFields()) try {
      if(irbody.hasAST()) {
        CtField field = irbody.getCtField();
        CtClass thisClass = field.getDeclaringClass();
        thisClass.removeField(field);
        thisClass.addField(field, irbody.getAST().toJavassistCode());
      }
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegenDefaultArgs(IR ir) {
    for(IRDefaultArgument irbody : ir.getDefaultArguments()) try {
      if(irbody.hasAST()) irbody.getCtMethod().setBody(irbody.getAST().toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegenStaticInitializer(IR ir) {
    for(IRStaticInitializer sinit : ir.getStaticInitializers()) try {
      if(sinit.hasAST()) sinit.getCtConstructor().insertAfter(sinit.getAST().toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private String target;
}

