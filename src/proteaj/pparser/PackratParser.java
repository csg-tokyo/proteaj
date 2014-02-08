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

  // TODO memoize env
  public TypedAST applyRule(SourceStringReader reader, Environment env, int pos) {
    if (! enable) return DISABLE;

    Pair<TypedAST, Integer> m = recall(reader, env, pos);
    if(m == null) {
      LR lr = new LR(this, lrStack);
      lrStack = lr;
      mtable.memoize(pos, lr, pos);

      reader.setPos(pos);
      TypedAST ans = parse(reader, env);

      lrStack = lrStack.getNext();

      if(lr.isDetected()) {
        lr.setSeed(ans);
        mtable.memoize(pos, lr, reader.getPos());
        return lrAnswer(reader, env, pos);
      }
      else {
        mtable.memoize(pos, ans, reader.getPos());
        return ans;
      }
    }
    else {
      reader.setPos(m.getSecond());

      if(m.getFirst() instanceof LR) {
        LR lr = (LR)m.getFirst();
        setupLR(lr);
        return lr.getSeed();
      }
      else return m.getFirst();
    }
  }

  private TypedAST growLR(Head h, SourceStringReader reader, Environment env, int pos) {
    heads.put(pos, h);
    while(true) {
      reader.setPos(pos);
      h.copyInvolvedSetToEvalSet();

      TypedAST ans = parse(reader, env);

      Pair<TypedAST, Integer> m = mtable.lookup(pos);
      int position = reader.getPos();

      if(ans.isFail() || position <= m.getSecond()) {
        heads.remove(pos);
        reader.setPos(m.getSecond());
        return m.getFirst();
      }
      else mtable.memoize(pos, ans, position);
    }
  }

  private void setupLR(LR lr) {
    if(! lr.isDetected()) lr.setHead(new Head(this));

    LR s = lrStack;
    while(s.getHead() != lr.getHead()) {
      s.setHead(lr.getHead());
      lr.getHead().addInvolvedParser(s.getParser());
      s = s.getNext();
    }
  }

  private TypedAST lrAnswer(SourceStringReader reader, Environment env, int pos) {
    Pair<TypedAST, Integer> m = mtable.lookup(pos);
    assert m.getFirst() instanceof LR;

    LR lr = (LR)m.getFirst();
    Head h = lr.getHead();

    if(h.getParser() != this) return lr.getSeed();
    mtable.memoize(pos, lr.getSeed(), m.getSecond());

    if(m.getFirst().isFail()) return FAIL;
    else return growLR(h, reader, env, pos);
  }

  private Pair<TypedAST, Integer> recall(SourceStringReader reader, Environment env, int pos) {
    if(! heads.containsKey(pos)) return mtable.lookup(pos);
    Head h = heads.get(pos);

    if(! (mtable.contains(pos) || h.isInvolved(this))) return new Pair<TypedAST, Integer>(FAIL, pos);

    Pair<TypedAST, Integer> ret;
    if(h.containsInEvalSet(this)) {
      h.removeFromEvalSet(this);

      TypedAST ans = parse(reader, env);
      mtable.memoize(pos, ans, reader.getPos());

      ret = new Pair<TypedAST, Integer>(ans, reader.getPos());
    }
    else ret = mtable.lookup(pos);

    return ret;
  }

  public void init() {
    enable = true;
    mtable.init();
  }

  public static void initialize() {
    lrStack = null;
    heads.clear();
  }

  public void disable() {
    enable = false;
  }

  protected PackratParser() {
    enable = false;
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

  private boolean enable;
  private MemoTable mtable;

  private static LR lrStack;
  private static Map<Integer, Head> heads = new HashMap<Integer, Head>();

  private static final BadAST FAIL = new BadAST(new FailLog("not involved in this left recursion", 0, 0));
  private static final BadAST DISABLE = new BadAST(new FailLog("disable parser", 0, 0));
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
  /*
  public Set<PackratParser> getInvolvedParser() {
    return involvedSet;
  }*/

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
    memos = new HashMap<Integer, Pair<TypedAST,Integer>>();
  }

  public void init() {
    memos.clear();
  }

  public void unmemoize(int pos) {
    memos.remove(pos);
  }

  public void memoize(int bPos, TypedAST ast, Integer ePos) {
    if(contains(bPos) && ! lookup(bPos).getFirst().isFail() && ! (lookup(bPos).getFirst() instanceof LR) && ast.isFail()) {
      return;
    }
    else memos.put(bPos, new Pair<TypedAST, Integer>(ast, ePos));
  }

  public boolean contains(int pos) {
    return memos.containsKey(pos);
  }

  public Pair<TypedAST, Integer> lookup(int pos) {
    return memos.get(pos);
  }

  private Map<Integer, Pair<TypedAST, Integer>> memos;
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
