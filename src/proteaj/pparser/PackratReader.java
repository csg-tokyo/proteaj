package proteaj.pparser;

import proteaj.util.Pair;

import java.util.*;

public class PackratReader {
  public PackratReader(String source, String filePath, int line) {
    this.source = source;
    this.filePath = filePath;
    this.current = 0;
    this.state = new PackratParserState();

    this.lines = createLinesMap(line);
  }

  public int getLine() {
    return getLine(current);
  }

  public int getLine(int pos) {
    return lines.lowerEntry(pos).getValue();
  }

  public boolean hasNext() {
    return current < source.length();
  }

  public boolean hasNext(int i) {
    return current + i < source.length();
  }

  public int lookahead() {
    if(hasNext()) return source.charAt(current);
    else return -1;
  }

  public int lookahead(int i) {
    if(hasNext(i)) return source.charAt(current + i);
    else return -1;
  }

  public char next() {
    assert hasNext();
    return source.charAt(current++);
  }

  public int getPos() {
    return current;
  }

  public void setPos(int pos) {
    current = pos;
  }

  public <T> MemoTable<T> memos (PackratParser<T> parser) {
    return state.getMemoTable(parser);
  }

  private TreeMap<Integer, Integer> createLinesMap(int line) {
    TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();

    map.put(-1, line);
    int length = source.length();

    for(int i = 0; i < length; i++) {
      char ch = source.charAt(i);
      if(ch == '\n') map.put(i, ++line);
    }

    return map;
  }

  private int current;

  private final String source;
  private final TreeMap<Integer, Integer> lines;

  public final String filePath;

  public final PackratParserState state;
}

class PackratParserState {
  public <T> void push (PackratParser<T> parser) { lrStack.push(new LR<T>(parser)); }
  public LR head () { return lrStack.peek(); }
  public LR pop () { return lrStack.pop(); }
  public LinkedList<LR> lrList () { return lrStack; }

  public <T> MemoTable<T> getMemoTable (PackratParser<T> parser) {
    if (! memoTables.containsKey(parser)) memoTables.put(parser, new MemoTable<>());
    return (MemoTable<T>) memoTables.get(parser);
  }

  Map<Integer, Head> heads = new HashMap<>();

  private LinkedList<LR> lrStack = new LinkedList<>();
  private Map<PackratParser<?>, MemoTable<?>> memoTables = new HashMap<>();
}

class MemoTable<T> {
  public MemoTable() {
    memos = new HashMap<>();
  }

  public void memoize(int bPos, ParseResult<T> ast, Integer ePos) {
    if (memos.containsKey(bPos)) {
      ParseResult<T> memo = memos.get(bPos)._1;

      if (! memo.isFail() && ! (memo instanceof LR) && ast.isFail()) return;
    }

    memos.put(bPos, Pair.make(ast, ePos));
  }

  public boolean contains(int pos) {
    return memos.containsKey(pos);
  }

  public Pair<ParseResult<T>, Integer> lookup(int pos) {
    return memos.get(pos);
  }

  private Map<Integer, Pair<ParseResult<T>, Integer>> memos;
}