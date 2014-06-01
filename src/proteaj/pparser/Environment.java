package proteaj.pparser;

import proteaj.error.*;
import proteaj.ir.IR;
import proteaj.ir.IRHeader;
import proteaj.tast.*;
import proteaj.env.type.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class Environment {
  public Environment(IR ir, CtMember thisMember) {
    this.thisClass = thisMember.getDeclaringClass();
    this.thisMember = thisMember;
    IRHeader header = ir.getIRHeader(thisClass);
    this.filePath = header.filePath;
    this.resolver = header.resolver;
    this.availableOperators = new AvailableOperators(header, ir.getOperatorPool());
    this.env = new HashMap<>();
    this.exceptions = new HashMap<>();

    if(! isStatic()) {
      Expression thisExpr = new ThisExpression(thisClass);
      add("this", thisExpr);

      try {
        Expression superExpr = new SuperExpression(thisClass);
        add("super", superExpr);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, filePath, 0));
      }

      for(CtField field : thisClass.getFields()) try {
        add(field.getName(), new FieldAccess(thisExpr, field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, filePath, 0));
      }

      for(CtField field : thisClass.getDeclaredFields()) try {
        add(field.getName(), new FieldAccess(thisExpr, field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, filePath, 0));
      }
    }

    else {
      for(CtField field : thisClass.getFields()) try {
        if(Modifiers.isStatic(field)) {
          add(field.getName(), new StaticFieldAccess(field));
        }
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, filePath, 0));
      }

      for(CtField field : thisClass.getDeclaredFields()) try {
        if(Modifiers.isStatic(field)) {
          add(field.getName(), new StaticFieldAccess(field));
        }
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, filePath, 0));
      }
    }
  }

  public Environment(Environment env) {
    this.thisClass = env.thisClass;
    this.thisMember = env.thisMember;
    this.filePath = env.filePath;
    this.resolver = env.resolver;
    this.availableOperators = env.availableOperators;
    this.env = new HashMap<>(env.env);
    this.exceptions = new HashMap<>();
  }

  public boolean isStatic() {
    return Modifiers.isStatic(thisMember);
  }

  public boolean isVisible(CtMember member) {
    return member.visibleFrom(thisClass);
  }

  public CtClass getType(String name) throws NotFoundError {
    return resolver.getType(name);
  }

  public CtClass getArrayType (String name, int dim) throws NotFoundError {
    return resolver.getArrayType(resolver.getType(name), dim);
  }

  public CtClass getArrayType (CtClass component, int dim) {
    return resolver.getArrayType(component, dim);
  }

  public boolean isTypeName(String name) {
    return resolver.isTypeName(name);
  }

  public List<CtMethod> getInstanceMethods (CtClass clazz, String name) {
    List<CtMethod> methods = new ArrayList<>();

    for (CtMethod method : getVisibleMethods(clazz, name)) {
      if (! Modifiers.isStatic(method)) methods.add(method);
    }

    return methods;
  }

  public List<CtMethod> getStaticMethods (CtClass clazz, String name) {
    List<CtMethod> methods = new ArrayList<>();

    for (CtMethod method : getVisibleMethods(clazz, name)) {
      if (Modifiers.isStatic(method)) methods.add(method);
    }

    return methods;
  }

  private List<CtMethod> getVisibleMethods (CtClass clazz, String name) {
    if (! visibleMethodsCache.containsKey(clazz)) {
      Map<String, List<CtMethod>> methods = new HashMap<>();

      if (clazz == thisClass) {
        for (CtMethod method : clazz.getDeclaredMethods()) addVisibleMethod(method, methods);
      }

      for (CtMethod method : clazz.getMethods()) {
        if (isVisible(method)) addVisibleMethod(method, methods);
      }

      // workaround
      if (clazz.getName().equals("java.lang.StringBuffer")) {
        for (CtMethod method : clazz.getDeclaredMethods()) {
          if (isVisible(method)) addVisibleMethod(method, methods);
        }
      }

      visibleMethodsCache.put(clazz, methods);
    }
    return visibleMethodsCache.get(clazz).getOrDefault(name, Collections.emptyList());
  }

  private void addVisibleMethod (CtMethod method, Map<String, List<CtMethod>> methods) {
    if (methods.containsKey(method.getName())) {
      List<CtMethod> list = methods.get(method.getName());

      if (list.contains(method)) {
        int i = list.indexOf(method);
        if (method != list.get(i)) try {
          if (method.getReturnType().subtypeOf(list.get(i).getReturnType())) {
            list.set(i, method);
          }
        } catch (NotFoundException e) { ErrorList.addError(new NotFoundError(e, filePath)); }
        return;
      }

      for (int i = 0; i < list.size(); i++) try {
        CtClass[] t1 = method.getParameterTypes();
        CtClass[] t2 = list.get(i).getParameterTypes();
        if (t1.length > t2.length) {
          list.add(i, method);
          return;
        }
        else if (t1.length == t2.length) {
          for (int j = 0; j < t1.length; j++) {
            if (t1[j].equals(t2[j])) continue;
            if (CtClassUtil.isSubtype(t1[j], t2[j])) {
              list.add(i, method);
              return;
            }
          }
        }
      } catch (NotFoundException e) { ErrorList.addError(new NotFoundError(e, filePath)); }
      list.add(method);
    }
    else {
      List<CtMethod> list = new ArrayList<>();
      list.add(method);
      methods.put(method.getName(), list);
    }
  }

  public void inheritExceptions(Environment env) {
    this.exceptions.putAll(env.exceptions);
  }

  public void add(String name, Expression expr) {
    env.put(name, expr);
  }

  public void declareLocal (String name, CtClass type) {
    env.put(name, new LocalVariable(name, type));
  }

  public void addExceptions(CtClass[] exceptions, int line) throws NotFoundException {
    for(CtClass e : exceptions) addException(e, line);
  }

  public void addException(CtClass exception, int line) throws NotFoundException {
    CommonTypes cts = CommonTypes.getInstance();
    assert exception.subtypeOf(cts.throwableType);

    if(exception.subtypeOf(cts.errorType)
        || exception.subtypeOf(cts.runtimeExceptionType)) return;

    if(! exceptions.containsKey(exception)) exceptions.put(exception, new ArrayList<>());
    exceptions.get(exception).add(line);
  }

  public void removeExceptions(CtClass[] exceptions) {
    for(CtClass e : exceptions) removeException(e);
  }

  public void removeException(CtClass clz) {
    Set<CtClass> removeExceptions = new HashSet<>();

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

  public Map<CtClass, List<Integer>> getExceptions() {
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
  public final String filePath;
  public final AvailableOperators availableOperators;

  private final TypeResolver resolver;
  private Map<String, Expression> env;
  private Map<CtClass, List<Integer>> exceptions;

  private Map<CtClass, Map<String, List<CtMethod>>> visibleMethodsCache = new HashMap<>();
}

