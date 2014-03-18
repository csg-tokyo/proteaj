package proteaj.ir;

import proteaj.error.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class Environment {
  public Environment(IR ir, CtMember thisMember) {
    this.thisClass = thisMember.getDeclaringClass();
    this.thisMember = thisMember;
    IRHeader header = ir.getIRHeader(thisClass);
    this.filePath = header.getFilePath();
    this.resolver = new TypeResolver(header, ir.getClassPool());
    this.operators = new UsingOperators(header, ir.getOperatorPool());
    this.env = new HashMap<String, Expression>();
    this.exceptions = new HashMap<CtClass, List<Integer>>();

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
    this.operators = env.operators;
    this.env = new HashMap<String, Expression>(env.env);
    this.exceptions = new HashMap<CtClass, List<Integer>>();
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
    return resolver.getArrayType(name, dim);
  }

  public CtClass getArrayType (CtClass component, int dim) throws NotFoundError {
    return resolver.getArrayType(component, dim);
  }

  public boolean isTypeName(String name) {
    return resolver.isTypeName(name);
  }

  public List<CtMethod> getInstanceMethods (CtClass clazz, String name) {
    List<CtMethod> methods = new ArrayList<CtMethod>();

    for (CtMethod method : getVisibleMethods(clazz)) {
      if (method.getName().equals(name) && (! Modifiers.isStatic(method))) methods.add(method);
    }

    return methods;
  }

  public List<CtMethod> getStaticMethods (CtClass clazz, String name) {
    List<CtMethod> methods = new ArrayList<CtMethod>();

    for (CtMethod method : getVisibleMethods(clazz)) {
      if (method.getName().equals(name) && Modifiers.isStatic(method)) methods.add(method);
    }

    return methods;
  }

  private List<CtMethod> getVisibleMethods (CtClass clazz) {
    if (! visibleMethodsCache.containsKey(clazz)) {
      List<CtMethod> methods = new ArrayList<CtMethod>();

      if (clazz == thisClass) {
        Collections.addAll(methods, clazz.getDeclaredMethods());
      }

      for (CtMethod method : clazz.getMethods()) {
        if (isVisible(method) && (! methods.contains(method))) methods.add(method);
      }

      visibleMethodsCache.put(clazz, methods);
    }
    return visibleMethodsCache.get(clazz);
  }

  public NavigableMap<Integer, List<IRPattern>> getPatterns(CtClass type) {
    return operators.getPatterns(type);
  }

  public NavigableMap<Integer, List<IRPattern>> getReadasPatterns(CtClass type) {
    return operators.getReadasPatterns(type);
  }

  public IROperator getOperator(CtClass type, int priority, IRPattern pattern) {
    return operators.getIROperator(type, priority, pattern);
  }

  public IROperator getReadasOperator(CtClass type, int priority, IRPattern pattern) {
    return operators.getIRReadasOperator(type, priority, pattern);
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

  private final TypeResolver resolver;
  private final UsingOperators operators;
  private Map<String, Expression> env;
  private Map<CtClass, List<Integer>> exceptions;

  private Map<CtClass, List<CtMethod>> visibleMethodsCache = new HashMap<CtClass, List<CtMethod>>();
}

