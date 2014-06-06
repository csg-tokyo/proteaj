package proteaj.pparser;

public class Failure<T> extends ParseResult<T> {
  public Failure(String msg, int pos, int line) {
    this(msg, pos, line, 0);
  }

  public Failure(String msg, int pos, int line, int priority) {
    this.msg = msg;
    this.pos = pos;
    this.line = line;
    this.priority = priority;
  }

  @Override
  public boolean isFail() {
    return true;
  }

  @Override
  public T getOrElse(T t) {
    return t;
  }

  public <U> Failure<U> fail() {
    return new Failure<U>(msg, pos, line, priority);
  }

  public final String msg;
  public final int pos;
  public final int line;
  public final int priority;
}
