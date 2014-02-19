package proteaj.ir;

import proteaj.error.*;
import proteaj.ir.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class Environment {
  public Environment(CtMember thisMember, String file) {
    this.thisClass = thisMember.getDeclaringClass();
    this.thisMember = thisMember;
    this.env = new HashMap<String, Expression>();
    this.exceptions = new HashMap<CtClass, List<Integer>>();

    if(! isStatic()) {
      Expression thisExpr = new ThisExpression(thisClass);
      add("this", thisExpr);

      try {
        Expression superExpr = new SuperExpression(thisClass);
        add("super", superExpr);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, file, 0));
      }

      for(CtField field : thisClass.getFields()) try {
        add(field.getName(), new FieldAccess(thisExpr, field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, file, 0));
      }

      for(CtField field : thisClass.getDeclaredFields()) try {
        add(field.getName(), new FieldAccess(thisExpr, field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, file, 0));
      }
    }

    else {
      for(CtField field : thisClass.getDeclaredFields()) try {
        if(Modifiers.isStatic(field.getModifiers())) {
          add(field.getName(), new StaticFieldAccess(field));
        }
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, file, 0));
      }
    }
  }

  public Environment(Environment env) {
    this.thisClass = env.thisClass;
    this.thisMember = env.thisMember;
    this.env = new HashMap<String, Expression>(env.env);
    this.exceptions = new HashMap<CtClass, List<Integer>>();
  }

  public boolean isStatic() {
    return Modifiers.isStatic(thisMember.getModifiers());
  }

  public void inheritExceptions(Environment env) {
    this.exceptions.putAll(env.exceptions);
  }

  public void add(String name, Expression expr) {
    env.put(name, expr);
  }

  public void addExceptions(CtClass[] exceptions, int line) throws NotFoundException {
    for(CtClass e : exceptions) addException(e, line);
  }

  public void addException(CtClass exception, int line) throws NotFoundException {
    assert exception.subtypeOf(IRCommonTypes.getThrowableType());

    if(exception.subtypeOf(IRCommonTypes.getErrorType())
        || exception.subtypeOf(IRCommonTypes.getRuntimeExceptionType())) return;

    if(! exceptions.containsKey(exception)) exceptions.put(exception, new ArrayList<Integer>());
    exceptions.get(exception).add(line);
  }

  public void removeExceptions(CtClass[] exceptions) {
    for(CtClass e : exceptions) removeException(e);
  }

  public void removeException(CtClass clz) {
    Set<CtClass> removeExceptions = new HashSet<CtClass>();

    for(CtClass exception : exceptions.keySet()) try {
      if(exception.subtypeOf(clz)) removeExceptions.add(exception);
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }

    for(CtClass exception : removeExceptions) {
      exceptions.remove(exception);
    }
  }

  public boolean hasException() {
    return ! exceptions.isEmpty();
  }

  public Map<CtClass, List<Integer>> getExceptionsData() {
    return exceptions;
  }

  public void addParams(String[] paramNames, CtClass[] paramTypes) {
    assert paramNames.length == paramTypes.length;
    for(int i = 0; i < paramNames.length; i++) {
      env.put(paramNames[i], new ParamAccess(paramNames[i], paramTypes[i], i));
    }
  }

  public boolean contains(String name) {
    return env.containsKey(name);
  }

  public Expression get(String name) {
    return env.get(name);
  }

  public final CtClass thisClass;
  public final CtMember thisMember;
  private Map<String, Expression> env;
  private Map<CtClass, List<Integer>> exceptions;
}

