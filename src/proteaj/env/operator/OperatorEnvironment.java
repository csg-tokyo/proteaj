package proteaj.env.operator;

import proteaj.error.*;
import proteaj.ir.*;

import java.util.*;
import javassist.*;

public abstract class OperatorEnvironment {
  public static RootOperatorEnvironment root() {
    return RootOperatorEnvironment.getInstance();
  }

  public static OperatorEnvironment onFile (String fileName, List<String> usingSyntax, Set<String> unusingSyntax) {
    return new OperatorEnvironment_File(fileName, usingSyntax, unusingSyntax);
  }

  public NavigableMap<Integer, List<IROperator>> getOperators (CtClass clazz) {
    initialize();
    if (normalMap.containsKey(clazz)) return normalMap.get(clazz);
    else return Collections.emptyNavigableMap();
  }

  public NavigableMap<Integer, List<IROperator>> getReadAsOperators (CtClass clazz) {
    initialize();
    if (readAsMap.containsKey(clazz)) return readAsMap.get(clazz);
    else return Collections.emptyNavigableMap();
  }

  protected abstract List<IRSyntax> getSyntaxList();
  protected abstract Set<IRSyntax> getExcludingSyntax();
  protected abstract NotFoundError makeError (String name);

  private void initialize () {
    if (normalMap == null || readAsMap == null) {
      int basePriority = 0;
      for (IRSyntax syntax : getSyntaxList()) {
        basePriority = loadSyntax(syntax, basePriority) + 1;
      }
    }
  }

  // under construction
  private int loadSyntax (IRSyntax syntax, int basePriority) {
    if (getExcludingSyntax().contains(syntax)) return basePriority;

    int maxPriority = basePriority;
    if (syntax.hasBaseIRSyntax()) maxPriority = loadSyntax(syntax.getBaseIRSyntax(), basePriority);

    for (IROperator operator : syntax.getOperators()) {
      int priority = operator.priority + basePriority;
      if (maxPriority < priority) maxPriority = priority;

      if (! operator.pattern.isReadas()) loadOperator(operator, priority, normalMap);
      else loadOperator(operator, priority, readAsMap);
    }

    return maxPriority;
  }

  private void loadOperator (IROperator operator, int priority, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    final CtClass clazz = operator.returnType;

    if (clazz.isPrimitive()) loadPrimitive(clazz, priority, operator, map);
    else loadRef(clazz, priority, operator, map);
  }

  private void loadRef (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    addOperatorToMap(clazz, priority, operator, map);
    loadRef_SuperClass(clazz, priority, operator, map);
    loadRef_Interfaces(clazz, priority, operator, map);
  }

  private void loadRef_SuperClass (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    if (operator.returnTypeBounds.contains(clazz)) return;
    final CtClass sup;
    try { sup = clazz.getSuperclass(); } catch (NotFoundException e) {
      ErrorList.addError(makeError("super class of " + clazz.getName()));
      return;
    }
    if (sup != null) loadRef(sup, priority, operator, map);
  }

  private void loadRef_Interfaces (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    if (operator.returnTypeBounds.contains(clazz)) return;
    final CtClass[] interfaces;
    try { interfaces = clazz.getInterfaces(); } catch (NotFoundException e) {
      ErrorList.addError(makeError("interface of " + clazz.getName()));
      return;
    }
    for (CtClass ifc : interfaces) loadRef_Interface(ifc, priority, operator, map);
  }

  private void loadRef_Interface(CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    addOperatorToMap(clazz, priority, operator, map);
    loadRef_Interfaces(clazz, priority, operator, map);
  }

  private void loadPrimitive (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    addOperatorToMap(clazz, priority, operator, map);
/*
    if (clazz == CtClass.byteType) loadPrimitive(CtClass.shortType, priority, operator, map);
    else if (clazz == CtClass.charType) loadPrimitive(CtClass.intType, priority, operator, map);
    else if (clazz == CtClass.shortType) loadPrimitive(CtClass.intType, priority, operator, map);
    else if (clazz == CtClass.intType) loadPrimitive(CtClass.longType, priority, operator, map);
    else if (clazz == CtClass.longType) loadPrimitive(CtClass.floatType, priority, operator, map);
    else if (clazz == CtClass.floatType) loadPrimitive(CtClass.doubleType, priority, operator, map);*/
  }

  private static void addOperatorToMap (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    List<IROperator> operators = getEntryFromMap(clazz, priority, map);
    operators.add(operator);
  }

  private static List<IROperator> getEntryFromMap (CtClass clazz, int priority, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    if (! map.containsKey(clazz)) map.put(clazz, new TreeMap<>());
    TreeMap<Integer, List<IROperator>> treeMap = map.get(clazz);
    if (! treeMap.containsKey(priority)) treeMap.put(priority, new ArrayList<>());
    return treeMap.get(priority);
  }

  private Map<CtClass, TreeMap<Integer, List<IROperator>>> normalMap = null;
  private Map<CtClass, TreeMap<Integer, List<IROperator>>> readAsMap = null;
}
