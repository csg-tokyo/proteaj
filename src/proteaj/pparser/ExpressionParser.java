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
      CtClass rtype = usops.getIROperator(type, priority, pattern).getReturnType();
      if(rtype != type) {
        op = OperationParser.getParser(rtype, priority, pattern).applyRule(reader, env, pos);
        if(! op.isFail()) return op;
      }

      if(flog == null) flog = op.getFailLog();
      else flog = chooseBest(flog, op.getFailLog());
    }

    TypedAST op = getParser(type, priority, false).applyRule(reader, env, pos);
    if(! op.isFail()) return op;

    if(flog == null) flog = op.getFailLog();
    else flog = chooseBest(flog, op.getFailLog());

    return new BadAST(flog);
  }

  public static ExpressionParser getParser(CtClass type) {
    if(! parsers.containsKey(type)) registerOperators(type);

    TreeMap<Integer, ExpressionParser> tmap = parsers.get(type);
    if(tmap.isEmpty()) return DefaultExpressionParser.getParser(type);
    else return tmap.firstEntry().getValue();
  }

  public static ExpressionParser getParser(CtClass type, int priority, boolean inclusive) {
    if(! parsers.containsKey(type)) registerOperators(type);

    TreeMap<Integer, ExpressionParser> tmap = parsers.get(type);

    Entry<Integer, ExpressionParser> entry;
    if(inclusive) entry = tmap.ceilingEntry(priority);
    else entry = tmap.higherEntry(priority);

    if(entry != null) return entry.getValue();
    else return DefaultExpressionParser.getParser(type);
  }

  public static void init(UsingOperators usops) {
    ExpressionParser.usops = usops;
    parsers.clear();
  }

  @Override
  public String toString() {
    return "ExpressionParser" + "[" + type.getName() + "]";
  }

  private static void registerOperators(CtClass type) {
    NavigableMap<Integer, List<IRPattern>> patternsMap = usops.getPatterns(type);

    if(! parsers.containsKey(type)) parsers.put(type, new TreeMap<Integer, ExpressionParser>());
    TreeMap<Integer, ExpressionParser> tmap = parsers.get(type);

    for(Map.Entry<Integer, List<IRPattern>> entry : patternsMap.entrySet()) {
      ExpressionParser parser = new ExpressionParser(type, entry.getValue(), entry.getKey());
      tmap.put(entry.getKey(), parser);
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

  private static Map<CtClass, TreeMap<Integer, ExpressionParser>> parsers = new HashMap<CtClass, TreeMap<Integer,ExpressionParser>>();

  private static UsingOperators usops;
}