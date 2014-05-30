package proteaj.pparser;

import proteaj.error.*;
import proteaj.util.*;

import java.util.*;

public abstract class PackratParser<T> {
  protected abstract ParseResult<T> parse(PackratReader reader, Environment env);

  public ParseResult<T> applyRule(PackratReader reader, Environment env) {
    return applyRule(reader, env, reader.getPos());
  }

  public ParseResult<T> applyRule(PackratReader reader, Environment env, int pos) {
    Pair<ParseResult<T>, Integer> m = recall(reader, env, pos);
    if(m == null) {
      reader.state.push(this);
      LR<T> lr = reader.state.head();
      mtable(reader).memoize(pos, lr, pos);

      reader.setPos(pos);
      ParseResult<T> ans = parse(reader, env);

      reader.state.pop();

      if(lr.isDetected()) {
        lr.setSeed(ans);
        mtable(reader).memoize(pos, lr, reader.getPos());
        return lrAnswer(reader, env, pos, lr, reader.getPos());
      }
      else {
        mtable(reader).memoize(pos, ans, reader.getPos());
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

  private ParseResult<T> lrAnswer(PackratReader reader, Environment env, int bPos, LR<T> lr, int ePos) {
    Head h = lr.getHead();

    if(h.parser != this) return lr.getSeed();
    mtable(reader).memoize(bPos, lr.getSeed(), ePos);

    return growLR(h, reader, env, bPos);
  }

  private ParseResult<T> growLR(Head h, PackratReader reader, Environment env, int pos) {
    reader.state.heads.put(pos, h);
    while(true) {
      reader.setPos(pos);
      h.copyInvolvedSetToEvalSet();

      ParseResult<T> ans = parse(reader, env);

      Pair<ParseResult<T>, Integer> m = mtable(reader).lookup(pos);
      int position = reader.getPos();

      if(ans.isFail() || position <= m._2) {
        reader.state.heads.remove(pos);
        reader.setPos(m._2);
        return m._1;
      }
      else mtable(reader).memoize(pos, ans, position);
    }
  }

  private void setupLR(PackratReader reader, LR lr) {
    if(! lr.isDetected()) lr.setHead(new Head<>(this));

    for (LR s : reader.state.lrList()) {
      if (s.getHead() == lr.getHead()) return;
      s.setHead(lr.getHead());
      lr.getHead().addInvolvedParser(s.parser);
    }

    assert false;
  }

  private Pair<ParseResult<T>, Integer> recall(PackratReader reader, Environment env, int pos) {
    if(! reader.state.heads.containsKey(pos)) return mtable(reader).lookup(pos);
    Head h = reader.state.heads.get(pos);

    if(! (mtable(reader).contains(pos) || h.involves(this))) return Pair.make(FAIL, pos);

    if(h.containsInEvalSet(this)) {
      h.removeFromEvalSet(this);

      ParseResult<T> ans = parse(reader, env);
      mtable(reader).memoize(pos, ans, reader.getPos());

      return Pair.make(ans, reader.getPos());
    }
    else return mtable(reader).lookup(pos);
  }

  protected PackratParser() {}

  // utilities
  protected Success<T> success(T t) {
    return new Success<>(t);
  }

  protected Failure<T> fail(String msg, int pos, PackratReader reader) {
    Failure<T> f = new Failure<>(msg, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return f;
  }

  protected <U> Failure<T> fail(ParseResult<U> result, int pos, PackratReader reader) {
    assert result.isFail();
    reader.setPos(pos);
    return ((Failure<U>)result).fail();
  }

  protected Failure<T> fail(List<ParseResult<?>> results, int pos, PackratReader reader) {
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

  private MemoTable<T> mtable(PackratReader reader) { return reader.memos(this); }

  private final ParseResult<T> FAIL = new Failure<>("not involved in this left recursion", 0, 0);
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