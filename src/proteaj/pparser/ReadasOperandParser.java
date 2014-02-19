package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import java.util.Map.Entry;
import javassist.*;

public class ReadasOperandParser extends PackratParser {
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    FailLog flog = null;

    for(IRPattern pattern : patterns) {
      TypedAST op = ReadasOperationParser.getParser(type, priority, pattern).applyRule(reader, env, pos);
      if(! op.isFail()) return op;

      if(flog == null) flog = op.getFailLog();
      else flog = chooseBest(flog, op.getFailLog());
    }

    TypedAST op = getParser(type, priority, false).applyRule(reader, env, pos);
    if(! op.isFail()) return op;

    if(flog == null) flog = op.getFailLog();
    else flog = chooseBest(flog, op.getFailLog());

    reader.setPos(pos);
    return new BadAST(flog);
  }

  public static ReadasOperandParser getParser(CtClass type) {
    if(! parsers.containsKey(type)) registerOperators(type);

    TreeMap<Integer, ReadasOperandParser> tmap = parsers.get(type);
    if(tmap.isEmpty()) return DefaultReadasOperandParser.getParser(type);
    else return tmap.firstEntry().getValue();
  }

  public static ReadasOperandParser getParser(CtClass type, int priority, boolean inclusive) {
    if(! parsers.containsKey(type)) registerOperators(type);

    TreeMap<Integer, ReadasOperandParser> tmap = parsers.get(type);

    Entry<Integer, ReadasOperandParser> entry;
    if(inclusive) entry = tmap.ceilingEntry(priority);
    else entry = tmap.higherEntry(priority);

    if(entry != null) return entry.getValue();
    else return DefaultReadasOperandParser.getParser(type);
  }

  public static void init(UsingOperators usops) {
    ReadasOperandParser.usops = usops;
    parsers.clear();
  }

  private static void registerOperators(CtClass type) {
    NavigableMap<Integer, List<IRPattern>> patternsMap = usops.getReadasPatterns(type);

    if(! parsers.containsKey(type)) parsers.put(type, new TreeMap<Integer, ReadasOperandParser>());
    TreeMap<Integer, ReadasOperandParser> tmap = parsers.get(type);

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

  private static Map<CtClass, TreeMap<Integer, ReadasOperandParser>> parsers = new HashMap<CtClass, TreeMap<Integer,ReadasOperandParser>>();
  private static UsingOperators usops;
}
