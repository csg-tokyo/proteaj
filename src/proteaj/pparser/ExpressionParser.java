package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class ExpressionParser extends PackratParser {
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    FailLog flog = null;
    for(IRPattern pattern : patterns) {
      TypedAST op = OperationParser.getParser(type, priority, pattern).applyRule(reader, env, pos);
      if(! op.isFail()) return op;

      // The translation for supporting sub-type arguments described in our paper creates a lot of operators.
      // Because it causes overhead, I take another solution.
      //
      // if "type" is a super type of "rtype" and "pattern" has a left recursion, first time analysis will always fail.
      // (ad-hoc solution. I'm not sure that it is a complete solution.)
      CtClass rtype = env.getOperator(type, priority, pattern).getReturnType();
      if(rtype != type) {
        op = OperationParser.getParser(rtype, priority, pattern).applyRule(reader, env, pos);
        if(! op.isFail()) return op;
      }

      if(flog == null) flog = op.getFailLog();
      else flog = chooseBest(flog, op.getFailLog());
    }

    TypedAST op = getParser(type, priority, false, env).applyRule(reader, env, pos);
    if(! op.isFail()) return op;

    if(flog == null) flog = op.getFailLog();
    else flog = chooseBest(flog, op.getFailLog());

    return new BadAST(flog);
  }

  public static ExpressionParser getParser(CtClass type, Environment env) {
    TreeMap<Integer, ExpressionParser> tmap = getFromCache(type, env);

    if(tmap.isEmpty()) return DefaultExpressionParser.getParser(type);
    else return tmap.firstEntry().getValue();
  }

  public static ExpressionParser getParser(CtClass type, int priority, boolean inclusive, Environment env) {
    TreeMap<Integer, ExpressionParser> tmap = getFromCache(type, env);

    Entry<Integer, ExpressionParser> entry = inclusive ? tmap.ceilingEntry(priority) : tmap.higherEntry(priority);

    if(entry != null) return entry.getValue();
    else return DefaultExpressionParser.getParser(type);
  }

  @Override
  public String toString() {
    return "ExpressionParser" + "[" + type.getName() + "]";
  }

  private static TreeMap<Integer, ExpressionParser> getFromCache(CtClass type, Environment env) {
    if (! parsers.containsKey(env) || ! parsers.get(env).containsKey(type)) {
      registerOperators(type, env);
    }
    return parsers.get(env).get(type);
  }

  private static void registerOperators(CtClass type, Environment env) {
    NavigableMap<Integer, List<IRPattern>> patternsMap = env.getPatterns(type);

    if (! parsers.containsKey(env)) parsers.put(env, new HashMap<CtClass, TreeMap<Integer, ExpressionParser>>());
    Map<CtClass, TreeMap<Integer, ExpressionParser>> map = parsers.get(env);

    if (! map.containsKey(type)) map.put(type, new TreeMap<Integer, ExpressionParser>());
    TreeMap<Integer, ExpressionParser> tMap = map.get(type);

    for(Map.Entry<Integer, List<IRPattern>> entry : patternsMap.entrySet()) {
      ExpressionParser parser = new ExpressionParser(type, entry.getValue(), entry.getKey());
      tMap.put(entry.getKey(), parser);
    }
  }

  private ExpressionParser(CtClass type, List<IRPattern> patterns, int priority) {
    this.type = type;
    this.patterns = patterns;
    this.priority = priority;
  }

  protected ExpressionParser(CtClass type) {
    this.type = type;
    this.patterns = Collections.emptyList();
    this.priority = Integer.MAX_VALUE;
  }

  protected CtClass type;
  private List<IRPattern> patterns;
  private int priority;

  private static Map<Environment, Map<CtClass, TreeMap<Integer, ExpressionParser>>> parsers =
      new WeakHashMap<Environment, Map<CtClass, TreeMap<Integer, ExpressionParser>>>();
}