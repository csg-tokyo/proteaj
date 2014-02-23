package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class ReadasOperandParser extends PackratParser<Expression> {
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    List<ParseResult<?>> fails = new ArrayList<ParseResult<?>>();

    for(IRPattern pattern : patterns) {
      ParseResult<Expression> op = ReadasOperationParser.getParser(type, priority, pattern).applyRule(reader, env, pos);
      if(! op.isFail()) return op;
      else fails.add(op);
    }

    ParseResult<Expression> op = getParser(type, priority, false, env).applyRule(reader, env, pos);
    if(! op.isFail()) return op;
    else fails.add(op);

    return fail(fails, pos, reader);
  }

  public static ReadasOperandParser getParser(CtClass type, Environment env) {
    TreeMap<Integer, ReadasOperandParser> tmap = getFromCache(type, env);

    if(tmap.isEmpty()) return DefaultReadasOperandParser.getParser(type);
    else return tmap.firstEntry().getValue();
  }

  public static ReadasOperandParser getParser(CtClass type, int priority, boolean inclusive, Environment env) {
    final TreeMap<Integer, ReadasOperandParser> tmap = getFromCache(type, env);
    final Entry<Integer, ReadasOperandParser> entry = inclusive ? tmap.ceilingEntry(priority) : tmap.higherEntry(priority);

    if(entry != null) return entry.getValue();
    else return DefaultReadasOperandParser.getParser(type);
  }

  private static TreeMap<Integer, ReadasOperandParser> getFromCache(CtClass type, Environment env) {
    if (! parsers.containsKey(env) || ! parsers.get(env).containsKey(type)) {
      registerOperators(type, env);
    }
    return parsers.get(env).get(type);
  }

  private static void registerOperators(CtClass type, Environment env) {
    NavigableMap<Integer, List<IRPattern>> patternsMap = env.getReadasPatterns(type);

    if (! parsers.containsKey(env)) parsers.put(env, new HashMap<CtClass, TreeMap<Integer, ReadasOperandParser>>());
    Map<CtClass, TreeMap<Integer, ReadasOperandParser>> map = parsers.get(env);

    if(! map.containsKey(type)) map.put(type, new TreeMap<Integer, ReadasOperandParser>());
    TreeMap<Integer, ReadasOperandParser> tmap = map.get(type);

    for(Map.Entry<Integer, List<IRPattern>> entry : patternsMap.entrySet()) {
      ReadasOperandParser parser = new ReadasOperandParser(type, entry.getValue(), entry.getKey());
      tmap.put(entry.getKey(), parser);
    }
  }

  private ReadasOperandParser(CtClass type, List<IRPattern> patterns, int priority) {
    this.type = type;
    this.patterns = patterns;
    this.priority = priority;
  }

  protected ReadasOperandParser(CtClass type) {
    this.type = type;
    this.patterns = Collections.emptyList();
    this.priority = -2;
  }

  protected CtClass type;
  private List<IRPattern> patterns;
  private int priority;

  private static Map<Environment, Map<CtClass, TreeMap<Integer, ReadasOperandParser>>> parsers =
      new WeakHashMap<Environment, Map<CtClass, TreeMap<Integer, ReadasOperandParser>>>();
}
