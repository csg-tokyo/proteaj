package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;
import proteaj.util.*;

import java.util.*;

public abstract class PackratParser {
  protected abstract TypedAST parse(SourceStringReader reader, Environment env);

  public TypedAST applyRule(SourceStringReader reader, Environment env) {
    return applyRule(reader, env, reader.getPos());
  }

  public TypedAST applyRule(SourceStringReader reader, Environment env, int pos) {
    Pair<TypedAST, Integer> m = recall(reader, env, pos);
    if(m == null) {
      LR lr = new LR(this, getState(reader).lrStack);
      getState(reader).lrStack = lr;
      mtable.memoize(reader, pos, lr, pos);

      reader.setPos(pos);
      TypedAST ans = parse(reader, env);

      getState(reader).lrStack = getState(reader).lrStack.getNext();

      if(lr.isDetected()) {
        lr.setSeed(ans);
        mtable.memoize(reader, pos, lr, reader.getPos());
        return lrAnswer(reader, env, pos);
      }
      else {
        mtable.memoize(reader, pos, ans, reader.getPos());
        return ans;
      }
    }
    else {
      reader.setPos(m.getSecond());

      if(m.getFirst() instanceof LR) {
        LR lr = (LR)m.getFirst();
        setupLR(reader, lr);
        return lr.getSeed();
      }
      else return m.getFirst();
    }
  }

  private TypedAST growLR(Head h, SourceStringReader reader, Environment env, int pos) {
    getState(reader).heads.put(pos, h);
    while(true) {
      reader.setPos(pos);
      h.copyInvolvedSetToEvalSet();

      TypedAST ans = parse(reader, env);

      Pair<TypedAST, Integer> m = mtable.lookup(reader, pos);
      int position = reader.getPos();

      if(ans.isFail() || position <= m.getSecond()) {
        getState(reader).heads.remove(pos);
        reader.setPos(m.getSecond());
        return m.getFirst();
      }
      else mtable.memoize(reader, pos, ans, position);
    }
  }

  private void setupLR(SourceStringReader reader, LR lr) {
    if(! lr.isDetected()) lr.setHead(new Head(this));

    LR s = getState(reader).lrStack;
    while(s.getHead() != lr.getHead()) {
      s.setHead(lr.getHead());
      lr.getHead().addInvolvedParser(s.getParser());
      s = s.getNext();
    }
  }

  private TypedAST lrAnswer(SourceStringReader reader, Environment env, int pos) {
    Pair<TypedAST, Integer> m = mtable.lookup(reader, pos);
    assert m.getFirst() instanceof LR;

    LR lr = (LR)m.getFirst();
    Head h = lr.getHead();

    if(h.getParser() != this) return lr.getSeed();
    mtable.memoize(reader, pos, lr.getSeed(), m.getSecond());

    if(m.getFirst().isFail()) return FAIL;
    else return growLR(h, reader, env, pos);
  }

  private Pair<TypedAST, Integer> recall(SourceStringReader reader, Environment env, int pos) {
    if(! getState(reader).heads.containsKey(pos)) return mtable.lookup(reader, pos);
    Head h = getState(reader).heads.get(pos);

    if(! (mtable.contains(reader, pos) || h.isInvolved(this))) return new Pair<TypedAST, Integer>(FAIL, pos);

    Pair<TypedAST, Integer> ret;
    if(h.containsInEvalSet(this)) {
      h.removeFromEvalSet(this);

      TypedAST ans = parse(reader, env);
      mtable.memoize(reader, pos, ans, reader.getPos());

      ret = new Pair<TypedAST, Integer>(ans, reader.getPos());
    }
    else ret = mtable.lookup(reader, pos);

    return ret;
  }

  protected PackratParser() {
    mtable = new MemoTable();
  }

  // utilities
  protected FailLog chooseBest(FailLog... failLogs) {
    FailLog best = failLogs[0];

    for(int i = 1; i < failLogs.length; i++) {
      if(best.getEndPosition() < failLogs[i].getEndPosition()) best = failLogs[i];
    }

    return best;
  }

  private PackratParserState getState (SourceStringReader reader) {
    if (! state.containsKey(reader)) state.put(reader, new PackratParserState());
    return state.get(reader);
  }

  private MemoTable mtable;

  private static final Map<SourceStringReader, PackratParserState> state = new WeakHashMap<SourceStringReader, PackratParserState>();
  private static final BadAST FAIL = new BadAST(new FailLog("not involved in this left recursion", 0, 0));

  private static class PackratParserState {
    LR lrStack = null;
    Map<Integer, Head> heads = new HashMap<Integer, Head>();
  }
}

abstract class ComposedParser_Sequential extends PackratParser {
  public ComposedParser_Sequential(String name) {
    this.name = name;
  }

  protected abstract PackratParser[] getParsers(Environment env);
  protected abstract TypedAST makeAST(int pos, int line, String file, TypedAST... as);

  @Override
  protected final TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser[] ps = getParsers(env);
    TypedAST[] as = new TypedAST[ps.length];
    for (int i = 0; i < ps.length; i++) {
      as[i] = ps[i].applyRule(reader, env);
      if (as[i].isFail()) {
        reader.setPos(pos);
        return as[i];
      }
    }
    TypedAST ret = makeAST(reader.getPos(), reader.getLine(), reader.getFilePath(), as);
    if (ret.isFail()) reader.setPos(pos);
    return ret;
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}

abstract class ComposedParser_Alternative extends PackratParser {
  public ComposedParser_Alternative(String name) {
    this.name = name;
  }

  protected abstract PackratParser[] getParsers(Environment env);

  @Override
  protected final TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser[] ps = getParsers(env);
    FailLog flog = new FailLog("Suitable parser is not found", pos, reader.getLine());

    for (PackratParser p : ps) {
      TypedAST ret = p.applyRule(reader, env, pos);
      if (! ret.isFail()) return ret;
      else if (flog == null || flog.getEndPosition() < ret.getFailLog().getEndPosition()) flog = ret.getFailLog();
    }

    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}

abstract class ComposedParser_Repetition extends PackratParser {
  public ComposedParser_Repetition(String name) {
    this.name = name;
  }

  protected abstract PackratParser getParser(Environment env);
  protected abstract TypedAST makeAST(int pos, int line, String file, List<TypedAST> as);

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser p = getParser(env);
    List<TypedAST> list = new ArrayList<TypedAST>();

    while(true) {
      TypedAST ast = p.applyRule(reader, env);
      if (ast.isFail()) break;

      list.add(ast);
      pos = reader.getPos();
    }

    reader.setPos(pos);
    return makeAST(pos, reader.getLine(), reader.getFilePath(), list);
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}

class LR extends TypedAST {
  public LR(PackratParser parser, LR next) {
    this.seed = FAIL;
    this.parser = parser;
    this.head = null;
    this.next = next;
  }

  public boolean isDetected() {
    return head != null;
  }

  public TypedAST getSeed() {
    return seed;
  }

  public PackratParser getParser() {
    return parser;
  }

  public Head getHead() {
    return head;
  }

  public LR getNext() {
    return next;
  }

  public void setSeed(TypedAST seed) {
    this.seed = seed;
  }

  public void setHead(Head head) {
    this.head = head;
  }

  @Override
  public String toJavassistCode() {
    return null;
  }

  @Override
  public String toString() {
    return "LR : " + parser + " : seed : " + seed + " : head : " + head;
  }

  private TypedAST seed;
  private PackratParser parser;
  private Head head;
  private LR next;

  private static final BadAST FAIL = new BadAST(new FailLog("Left recursion seed", 0, 0));
}

class Head {
  public Head(PackratParser parser) {
    this.parser = parser;
    this.involvedSet = new ArrayList<PackratParser>();
    this.evalSet = new ArrayList<PackratParser>();
  }

  public PackratParser getParser() {
    return parser;
  }

  public void addInvolvedParser(PackratParser parser) {
    involvedSet.add(parser);
  }

  public boolean isInvolved(PackratParser parser) {
    return this.parser == parser || involvedSet.contains(parser);
  }

  public boolean containsInEvalSet(PackratParser parser) {
    return evalSet.contains(parser);
  }

  public void removeFromEvalSet(PackratParser parser) {
    evalSet.remove(parser);
  }

  public void copyInvolvedSetToEvalSet() {
    evalSet = new ArrayList<PackratParser>(involvedSet);
  }

  @Override
  public String toString() {
    return parser.toString() + "{ " + involvedSet + " }";
  }

  private PackratParser parser;
  private List<PackratParser> involvedSet;
  private List<PackratParser> evalSet;
}

class MemoTable {
  public MemoTable() {
    memos = new WeakHashMap<SourceStringReader, Map<Integer, Pair<TypedAST, Integer>>>();
  }

  public void init() {
    memos.clear();
  }

  public void memoize(SourceStringReader reader, int bPos, TypedAST ast, Integer ePos) {
    if (! memos.containsKey(reader)) memos.put(reader, new HashMap<Integer, Pair<TypedAST, Integer>>());

    Map<Integer, Pair<TypedAST, Integer>> map = memos.get(reader);

    if (map.containsKey(bPos)) {
      TypedAST memo = map.get(bPos).getFirst();

      if (! memo.isFail() && ! (memo instanceof LR) && ast.isFail()) return;
    }

    map.put(bPos, new Pair<TypedAST, Integer>(ast, ePos));
  }

  public boolean contains(SourceStringReader reader, int pos) {
    return memos.containsKey(reader) && memos.get(reader).containsKey(pos);
  }

  public Pair<TypedAST, Integer> lookup(SourceStringReader reader, int pos) {
    if (! memos.containsKey(reader)) return null;
    else return memos.get(reader).get(pos);
  }

  private Map<SourceStringReader, Map<Integer, Pair<TypedAST, Integer>>> memos;
}

class BadAST extends TypedAST {
  public BadAST(FailLog faillog) {
    this.faillog = faillog;
  }

  @Override
  public boolean isFail() {
    return true;
  }

  @Override
  public FailLog getFailLog() {
    return faillog;
  }

  @Override
  public String toJavassistCode() {
    return null;
  }

  @Override
  public String toString() {
    return "fail: " + faillog.getMessage();
  }

  private FailLog faillog;
}