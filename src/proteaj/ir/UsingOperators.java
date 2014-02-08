package proteaj.ir;

import proteaj.error.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class UsingOperators {
  public UsingOperators(IRHeader hdata, OperatorPool opool) {
    operators = new HashMap<CtClass, TreeMap<Integer, List<IRPattern>>>();
    patternToMethod = new HashMap<Triad<CtClass,Integer,IRPattern>, IROperator>();

    readasOperators = new HashMap<CtClass, TreeMap<Integer, List<IRPattern>>>();
    readasPatternToMethod = new HashMap<Triad<CtClass,Integer,IRPattern>, IROperator>();

    int basePriority = 0;
    List<String> ussyn = getUsingSyntax(hdata, opool);
    Collections.reverse(ussyn);

    for(String synName : ussyn) try {
      int maxPriority = loadSyntax(synName, basePriority, hdata, opool);
      basePriority += maxPriority + 100;
    } catch (NotFoundError e) {
      ErrorList.addError(e);
    }
  }

  public NavigableMap<Integer, List<IRPattern>> getPatterns(CtClass type) {
    if(operators.containsKey(type)) return operators.get(type);
    else return emptyMap;
  }

  public NavigableMap<Integer, List<IRPattern>> getReadasPatterns(CtClass type) {
    if(readasOperators.containsKey(type)) return readasOperators.get(type);
    else return emptyMap;
  }

  public IROperator getIROperator(CtClass type, int priority, IRPattern pattern) {
    return patternToMethod.get(new Triad<CtClass, Integer, IRPattern>(type, priority, pattern));
  }

  public IROperator getIRReadasOperator(CtClass type, int priority, IRPattern pattern) {
    return readasPatternToMethod.get(new Triad<CtClass, Integer, IRPattern>(type, priority, pattern));
  }

  private List<String> getUsingSyntax(IRHeader hdata, OperatorPool opool) {
    List<String> ussyn = new ArrayList<String>(hdata.getUsingSyntax());

    for(String synName : hdata.getUsingSyntax()) {
      while(opool.containsSyntax(synName)) {
        IRSyntax irsyn = opool.getSyntax(synName);
        if(irsyn.hasBaseSyntax()) {
          synName = irsyn.getBaseSyntax();
          if(ussyn.contains(synName)) ussyn.remove(synName);
        }
        else break;
      }
    }

    return ussyn;
  }

  private int loadSyntax(String synName, int basePriority, IRHeader hdata, OperatorPool opool) throws NotFoundError {
    if(! opool.containsSyntax(synName)) {
      throw new NotFoundError(synName + " is not found.", hdata.getFilePath(), 0);
    }

    if(hdata.getUnusingSyntax().contains(synName)) return 0;

    int maxPriority = 0;
    IRSyntax irsyn = opool.getSyntax(synName);
    if(irsyn.hasBaseSyntax()) maxPriority = loadSyntax(irsyn.getBaseSyntax(), basePriority, hdata, opool);

    for(IROperator odata : irsyn.getOperators()) {
      CtClass type = odata.getReturnType();
      int priority = odata.getPriority();
      IRPattern pat = odata.getPattern();

      if(maxPriority < priority) maxPriority = priority;

      if(pat.isReadas()) loadReadasOperator_itr(type, basePriority + priority, pat, odata, hdata);
      else loadOperator_itr(type, basePriority + priority, pat, odata, hdata);
    }
    return maxPriority;
  }

  private void loadOperator_itr(CtClass type, int priority, IRPattern pattern, IROperator irop, IRHeader hdata) {
    loadOperator(type, priority, pattern, irop);

    if(type.equals(CtClass.voidType)) return;

    try {
      if(! type.isInterface()) {
        CtClass superCls = type.getSuperclass();
        if(superCls != null) {
          loadOperator_itr(superCls, priority, pattern, irop, hdata);
        }
      }

      for(CtClass iface : type.getInterfaces()) {
        loadOperator_itr(iface, priority, pattern, irop, hdata);
      }
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, hdata.getFilePath(), 0));
    }
  }

  private void loadOperator(CtClass type, int priority, IRPattern pattern, IROperator irop) {
    if(! operators.containsKey(type)) {
      operators.put(type, new TreeMap<Integer, List<IRPattern>>());
    }
    TreeMap<Integer, List<IRPattern>> tmap = operators.get(type);

    if(! tmap.containsKey(priority)) {
      tmap.put(priority, new ArrayList<IRPattern>());
    }
    List<IRPattern> pats = tmap.get(priority);

    pats.add(pattern);
    patternToMethod.put(new Triad<CtClass, Integer, IRPattern>(type, priority, pattern), irop);
  }

  private void loadReadasOperator_itr(CtClass type, int priority, IRPattern pattern, IROperator irop, IRHeader hdata) {
    loadReadasOperator(type, priority, pattern, irop);
  }

  private void loadReadasOperator(CtClass type, int priority, IRPattern pattern, IROperator irop) {
    if(! readasOperators.containsKey(type)) {
      readasOperators.put(type, new TreeMap<Integer, List<IRPattern>>());
    }
    TreeMap<Integer, List<IRPattern>> tmap = readasOperators.get(type);

    if(! tmap.containsKey(priority)) {
      tmap.put(priority, new ArrayList<IRPattern>());
    }
    List<IRPattern> pats = tmap.get(priority);

    pats.add(pattern);
    readasPatternToMethod.put(new Triad<CtClass, Integer, IRPattern>(type, priority, pattern), irop);
  }

  private Map<CtClass, TreeMap<Integer, List<IRPattern>>> operators;
  private Map<Triad<CtClass, Integer, IRPattern>, IROperator> patternToMethod;

  private Map<CtClass, TreeMap<Integer, List<IRPattern>>> readasOperators;
  private Map<Triad<CtClass, Integer, IRPattern>, IROperator> readasPatternToMethod;

  private static final NavigableMap<Integer, List<IRPattern>> emptyMap = new TreeMap<Integer, List<IRPattern>>();
}
