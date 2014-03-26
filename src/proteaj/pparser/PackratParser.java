package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.util.*;

import java.util.*;

public abstract class PackratParser<T> {
  protected abstract ParseResult<T> parse(SourceStringReader reader, Environment env);

  public ParseResult<T> applyRule(SourceStringReader reader, Environment env) {
    return applyRule(reader, env, reader.getPos());
  }

  public ParseResult<T> applyRule(SourceStringReader reader, Environment env, int pos) {
    Pair<ParseResult<T>, Integer> m = recall(reader, env, pos);
    if(m == null) {
      getState(reader).push(this);
      LR<T> lr = getState(reader).head();
      mtable.memoize(reader, pos, lr, pos);

      reader.setPos(pos);
      ParseResult<T> ans = parse(reader, env);

      getState(reader).pop();

      if(lr.isDetected()) {
        lr.setSeed(ans);
        mtable.memoize(reader, pos, lr, reader.getPos());
        return lrAnswer(reader, env, pos, lr, reader.getPos());
      }
      else {
        mtable.memoize(reader, pos, ans, reader.getPos());
        return ans;
      }
    }
    else {
      reader.setPos(m._2);

      if(m._1 instanceof LR) {
        LR<T> lr = (LR<T>)m._1;
        setupLR(reader, lr);
        return lr.getSeed();
      }
      else return m._1;
    }
  }

  private ParseResult<T> lrAnswer(SourceStringReader reader, Environment env, int bPos, LR<T> lr, int ePos) {
    Head h = lr.getHead();

    if(h.parser != this) return lr.getSeed();
    mtable.memoize(reader, bPos, lr.getSeed(), ePos);

    return growLR(h, reader, env, bPos);
  }

  private ParseResult<T> growLR(Head h, SourceStringReader reader, Environment env, int pos) {
    getState(reader).heads.put(pos, h);
    while(true) {
      reader.setPos(pos);
      h.copyInvolvedSetToEvalSet();

      ParseResult<T> ans = parse(reader, env);

      Pair<ParseResult<T>, Integer> m = mtable.lookup(reader, pos);
      int position = reader.getPos();

      if(ans.isFail() || position <= m._2) {
        getState(reader).heads.remove(pos);
        reader.setPos(m._2);
        return m._1;
      }
      else mtable.memoize(reader, pos, ans, position);
    }
  }

  private void setupLR(SourceStringReader reader, LR lr) {
    if(! lr.isDetected()) lr.setHead(new Head<>(this));

    for (LR s : getState(reader).lrList()) {
      if (s.getHead() == lr.getHead()) return;
      s.setHead(lr.getHead());
      lr.getHead().addInvolvedParser(s.parser);
    }

    assert false;
  }

  private Pair<ParseResult<T>, Integer> recall(SourceStringReader reader, Environment env, int pos) {
    if(! getState(reader).heads.containsKey(pos)) return mtable.lookup(reader, pos);
    Head h = getState(reader).heads.get(pos);

    if(! (mtable.contains(reader, pos) || h.involves(this))) return Pair.make(FAIL, pos);

    if(h.containsInEvalSet(this)) {
      h.removeFromEvalSet(this);

      ParseResult<T> ans = parse(reader, env);
      mtable.memoize(reader, pos, ans, reader.getPos());

      return Pair.make(ans, reader.getPos());
    }
    else return mtable.lookup(reader, pos);
  }

  protected PackratParser() {
    mtable = new MemoTable<>();
  }

  // utilities
  protected Success<T> success(T t) {
    return new Success<>(t);
  }

  protected Failure<T> fail(String msg, int pos, SourceStringReader reader) {
    Failure<T> f = new Failure<>(msg, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return f;
  }

  protected <U> Failure<T> fail(ParseResult<U> result, int pos, SourceStringReader reader) {
    assert result.isFail();
    reader.setPos(pos);
    return ((Failure<U>)result).fail();
  }

  protected Failure<T> fail(List<ParseResult<?>> results, int pos, SourceStringReader reader) {
    assert ! results.isEmpty();

    Failure<T> best = null;
    for (ParseResult<?> r : results) {
      assert r.isFail();
      Failure<T> f = ((Failure<?>)r).fail();
      if (best == null || best.pos < f.pos) best = f;
    }

    reader.setPos(pos);
    return best;
  }

  private PackratParserState getState (SourceStringReader reader) {
    if (! state.containsKey(reader)) state.put(reader, new PackratParserState());
    return state.get(reader);
  }

  private MemoTable<T> mtable;

  private static final Map<SourceStringReader, PackratParserState> state = new WeakHashMap<>();
  private final ParseResult<T> FAIL = new Failure<>("not involved in this left recursion", 0, 0);
}

class PackratParserState {
  public <T> void push (PackratParser<T> parser) { lrStack.push(new LR<T>(parser)); }
  public LR head () { return lrStack.peek(); }
  public void pop () { lrStack.pop(); }
  public LinkedList<LR> lrList () { return lrStack; }

  private LinkedList<LR> lrStack = new LinkedList<>();
  Map<Integer, Head> heads = new HashMap<>();
}

class LR<T> extends ParseResult<T> {
  public LR(PackratParser parser) {
    this.seed = new Failure<>("left recursion seed", 0, 0);
    this.parser = parser;
    this.head = null;
  }

  public boolean isDetected() {
    return head != null;
  }

  public ParseResult<T> getSeed() {
    return seed;
  }

  public Head getHead() {
    return head;
  }

  public void setSeed(ParseResult<T> seed) {
    this.seed = seed;
  }

  public void setHead(Head head) {
    this.head = head;
  }

  @Override
  public boolean isFail() {
    return false;
  }

  @Override
  public FailLog getFailLog() {
    return null;
  }

  @Override
  public T getOrElse(T t) {
    return null;
  }

  @Override
  public String toString() {
    return "LR : " + parser + " : seed : " + seed + " : head : " + head;
  }

  private ParseResult<T> seed;
  private Head head;
  public final PackratParser parser;
}

class Head<T> {
  public Head(PackratParser<T> parser) {
    this.parser = parser;
    this.involvedSet = new ArrayList<>();
    this.evalSet = new ArrayList<>();
  }

  public void addInvolvedParser(PackratParser parser) {
    involvedSet.add(parser);
  }

  public boolean involves(PackratParser parser) {
    return this.parser == parser || involvedSet.contains(parser);
  }

  public boolean containsInEvalSet(PackratParser parser) {
    return evalSet.contains(parser);
  }

  public void removeFromEvalSet(PackratParser parser) {
    evalSet.remove(parser);
  }

  public void copyInvolvedSetToEvalSet() {
    evalSet = new ArrayList<>(involvedSet);
  }

  @Override
  public String toString() {
    return parser.toString() + "{ " + involvedSet + " }";
  }

  public final PackratParser<T> parser;
  private List<PackratParser> involvedSet;
  private List<PackratParser> evalSet;
}

class MemoTable<T> {
  public MemoTable() {
    memos = new WeakHashMap<>();
  }

  public void memoize(SourceStringReader reader, int bPos, ParseResult<T> ast, Integer ePos) {
    if (! memos.containsKey(reader)) memos.put(reader, new HashMap<Integer, Pair<ParseResult<T>, Integer>>());

    Map<Integer, Pair<ParseResult<T>, Integer>> map = memos.get(reader);

    if (map.containsKey(bPos)) {
      ParseResult<T> memo = map.get(bPos)._1;

      if (! memo.isFail() && ! (memo instanceof LR) && ast.isFail()) return;
    }

    map.put(bPos, Pair.make(ast, ePos));
  }

  public boolean contains(SourceStringReader reader, int pos) {
    return memos.containsKey(reader) && memos.get(reader).containsKey(pos);
  }

  public Pair<ParseResult<T>, Integer> lookup(SourceStringReader reader, int pos) {
    if (! memos.containsKey(reader)) return null;
    else return memos.get(reader).get(pos);
  }

  private Map<SourceStringReader, Map<Integer, Pair<ParseResult<T>, Integer>>> memos;
}