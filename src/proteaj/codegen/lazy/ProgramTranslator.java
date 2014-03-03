package proteaj.codegen.lazy;

import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class ProgramTranslator {
  public ProgramTranslator (Program program) {
    this.program = program;
  }

  public Program translate () {
    List<ClassDeclaration> generated = new ArrayList<ClassDeclaration>();

    for (OperatorModuleDeclaration op : program.getOperatorsModules()) {
      translate(op, generated);
    }

    for (ClassDeclaration clazz : program.getClasses()) {
      translate(clazz, generated);
    }

    program.addClasses(generated);

    return program;
  }

  private void translate (OperatorModuleDeclaration op, List<ClassDeclaration> generated) {
    for (IROperator operator : op.syntax.getOperators()) {
      translate(operator, generated);
    }
  }

  private void translate (IROperator operator, List<ClassDeclaration> generated) {
    IRPattern pattern = operator.getPattern();
    int length = pattern.getPatternLength();

    CtMethod actualMethod = operator.actualMethod;
    CtClass actualClass = actualMethod.getDeclaringClass();

    Map<Integer, CtMethod> lazyMap = new HashMap<Integer, CtMethod>();

    CtClass[] paramTypes;
    try { paramTypes = actualMethod.getParameterTypes(); }
    catch (NotFoundException e) { assert false; throw new RuntimeException(e); }

    for (int i = 0, n = 0; i < length; i++) {
      if (pattern.isOperand(i)) {
        if (pattern.isLazy(i)) {
          CtClass thunkType = actualClass.makeNestedClass(operator.getMethodName() + "$" + n, true);
          thunkType.setModifiers(thunkType.getModifiers() | Modifiers.ABSTRACT);

          CtMethod method = new CtMethod(pattern.getOperandType(i), "eval", new CtClass[0], thunkType);
          method.setModifiers(Modifiers.PUBLIC | Modifiers.ABSTRACT);

          try { thunkType.addMethod(method); }
          catch (CannotCompileException e) { assert false; throw new RuntimeException(e); }

          generated.add(new ClassDeclaration(thunkType, "auto generated"));

          paramTypes[n] = thunkType;

          lazyMap.put(n, method);
        }

        n++;
      }
    }

    if (! lazyMap.isEmpty()) try {
      CtMethod method = new CtMethod(actualMethod.getReturnType(), actualMethod.getName() + "$Lazy", paramTypes, actualClass);
      method.setModifiers(actualMethod.getModifiers());
      method.setExceptionTypes(actualMethod.getExceptionTypes());
      actualClass.addMethod(method);

      MethodDeclaration methodDcl = program.getClass(actualClass).getMethod(actualMethod);
      MethodBody body = new DefinitionTranslator(lazyMap).translate(methodDcl.body);
      program.addMethod(new MethodDeclaration(method, body));

    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void translate (ClassDeclaration clazz, List<ClassDeclaration> generated) {

  }

  private Program program;
}
