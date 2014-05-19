package proteaj.pparser;

import proteaj.error.*;

import java.util.*;
import javassist.*;
import proteaj.ir.IRHeader;
import proteaj.ir.IROperator;
import proteaj.ir.IRSyntax;
import proteaj.ir.OperatorPool;

public class AvailableOperators {
  public AvailableOperators (IRHeader header, OperatorPool pool) {
    this.normalMap = new HashMap<>();
    this.readAsMap = new HashMap<>();
    this.header = header;
    this.pool = pool;

    int basePriority = 0;

    for (IRSyntax syntax : getUsingList()) {
      basePriority = loadSyntax(syntax, basePriority) + 1;
    }

    //printForDebug();
  }

  public TreeMap<Integer, List<IROperator>> getOperators (CtClass clazz) {
    if (normalMap.containsKey(clazz)) return normalMap.get(clazz);
    else return emptyMap;
  }

  public TreeMap<Integer, List<IROperator>> getReadAsOperators (CtClass clazz) {
    if (readAsMap.containsKey(clazz)) return readAsMap.get(clazz);
    else return emptyMap;
  }

  private void printForDebug () {
    System.out.println("===========================================");
    for (Map.Entry<CtClass, TreeMap<Integer, List<IROperator>>> entry : normalMap.entrySet()) {
      System.out.println("* " + entry.getKey().getName());
      for (Map.Entry<Integer, List<IROperator>> entry1 : entry.getValue().entrySet()) {
        System.out.println(" # " + entry1.getKey());
        for (IROperator operator : entry1.getValue()) {
          System.out.println("  $ " + operator.pattern);
        }
      }
    }
  }

  /* private methods for initializing */
  private int loadSyntax (IRSyntax syntax, int basePriority) {
    if (header.unusingSyntax.contains(syntax.getName())) return basePriority;

    int maxPriority = basePriority;
    if (syntax.hasBaseSyntax()) try {
      maxPriority = loadSyntax(getSyntaxFromPool(syntax.getBaseSyntax()), basePriority);
    } catch (NotFoundError e) {
      ErrorList.addError(e);
      return basePriority;
    }

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
      ErrorList.addError(new NotFoundError(e, header.filePath, 0));
      return;
    }
    if (sup != null) loadRef(sup, priority, operator, map);
  }

  private void loadRef_Interfaces (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    if (operator.returnTypeBounds.contains(clazz)) return;
    final CtClass[] interfaces;
    try { interfaces = clazz.getInterfaces(); } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, header.filePath, 0));
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

  private List<IRSyntax> getUsingList () {
    List<String> using = new ArrayList<>(header.usingSyntax);
    for (String name : header.usingSyntax) try {
      IRSyntax syntax = getSyntaxFromPool(name);
      while (syntax.hasBaseSyntax()) {
        String base = syntax.getBaseSyntax();
        if (using.contains(base)) using.remove(base);
        syntax = getSyntaxFromPool(base);
      }
    } catch (NotFoundError e) {}

    List<IRSyntax> list = new ArrayList<>();
    for (String name : using) try {
      list.add(getSyntaxFromPool(name));
    } catch (NotFoundError e) {
      ErrorList.addError(e);
    }
    Collections.reverse(list);
    return list;
  }

  private IRSyntax getSyntaxFromPool (String name) throws NotFoundError {
    if (pool.containsSyntax(name)) return pool.getSyntax(name);
    else throw new NotFoundError("operators module " + name + " is not found", header.filePath, 0);
  }

  /* utility methods for manipulating operators map */
  private static void addOperatorToMap (CtClass clazz, int priority, IROperator operator, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    List<IROperator> operators = getEntryFromMap(clazz, priority, map);
    operators.add(operator);
  }

  private static List<IROperator> getEntryFromMap (CtClass clazz, int priority, Map<CtClass, TreeMap<Integer, List<IROperator>>> map) {
    if (! map.containsKey(clazz)) map.put(clazz, new TreeMap<Integer, List<IROperator>>());
    TreeMap<Integer, List<IROperator>> treeMap = map.get(clazz);
    if (! treeMap.containsKey(priority)) treeMap.put(priority, new ArrayList<IROperator>());
    return treeMap.get(priority);
  }

  // expected type, operator priority, operators, reference to super-type operators (type & parsing precedence)
  private Map<CtClass, TreeMap<Integer, List<IROperator>>> normalMap;
  private Map<CtClass, TreeMap<Integer, List<IROperator>>> readAsMap;

  private final IRHeader header;
  private final OperatorPool pool;

  private static TreeMap<Integer, List<IROperator>> emptyMap = new TreeMap<>();
}
