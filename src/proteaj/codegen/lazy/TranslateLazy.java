package proteaj.codegen.lazy;

import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class TranslateLazy {
  public TranslateLazy(Program program) {
    this.program = program;
  }

  public Program translate () {
    List<ClassDeclaration> generated = new ArrayList<ClassDeclaration>();
    Map<IROperator, Pair<CtMethod, Map<Integer, CtMethod>>> methods = new HashMap<IROperator, Pair<CtMethod, Map<Integer, CtMethod>>>();

    for (OperatorModuleDeclaration op : program.getOperatorsModules()) {
      translate(op, generated, methods);
    }

    for (ClassDeclaration clazz : program.getClasses()) {
      generated.add(translate(clazz, generated, methods));
    }

    return new Program(generated, program.getOperatorsModules());
  }

  private void translate (OperatorModuleDeclaration op, List<ClassDeclaration> generated, Map<IROperator, Pair<CtMethod, Map<Integer, CtMethod>>> methods) {
    for (IROperator operator : op.syntax.getOperators()) {
      translate(operator, generated, methods);
    }
  }

  private void translate (IROperator operator, List<ClassDeclaration> generated, Map<IROperator, Pair<CtMethod, Map<Integer, CtMethod>>> methods) {
    IRPattern pattern = operator.pattern;
    int length = pattern.getPatternLength();

    CtMethod actualMethod = operator.actualMethod;
    CtClass actualClass = actualMethod.getDeclaringClass();

    Map<Integer, CtMethod> lazyMap = new HashMap<>();

    CtClass[] paramTypes;
    try { paramTypes = actualMethod.getParameterTypes(); }
    catch (NotFoundException e) { assert false; throw new RuntimeException(e); }

    for (int i = 0, n = 0; i < length; i++) {
      if (pattern.isOperand(i)) {
        if (pattern.isLazy(i)) {
          CtClass thunkType = actualClass.makeNestedClass(operator.getMethodName() + n, true);
          thunkType.setModifiers(thunkType.getModifiers() | Modifiers.ABSTRACT);

          CtMethod method = new CtMethod(pattern.getOperandType(i), "eval", new CtClass[0], thunkType);
          method.setModifiers(Modifiers.PUBLIC | Modifiers.ABSTRACT);

          try { thunkType.addMethod(method); }
          catch (CannotCompileException e) { assert false; throw new RuntimeException(e); }

          generated.add(new ClassDeclaration(thunkType, "auto generated", Collections.emptyList()));

          paramTypes[n] = thunkType;

          lazyMap.put(n, method);
        }

        n++;
      }
    }

    if (! lazyMap.isEmpty()) try {
      CtMethod method = new CtMethod(actualMethod.getReturnType(), actualMethod.getName() + "_Lazy", paramTypes, actualClass);
      method.setModifiers(actualMethod.getModifiers());
      method.setExceptionTypes(actualMethod.getExceptionTypes());
      actualClass.addMethod(method);

      MethodDeclaration methodDcl = program.getClass(actualClass).getMethod(actualMethod);
      MethodBody body = new DefinitionTranslator(lazyMap).translate(methodDcl.body);
      program.addMethod(new MethodDeclaration(method, body));

      methods.put(operator, Pair.make(method, lazyMap));
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private ClassDeclaration translate (ClassDeclaration clazz, List<ClassDeclaration> generated, Map<IROperator, Pair<CtMethod, Map<Integer, CtMethod>>> methods) {
    return new ProgramTranslator(clazz.clazz, generated, methods).translate(clazz);
  }

  private Program program;
}
