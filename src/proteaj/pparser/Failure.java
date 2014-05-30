package proteaj.pparser;

import proteaj.error.FailLog;

public class Failure<T> extends ParseResult<T> {
  public Failure(String msg, int pos, int line) {
    this.msg = msg;
    this.pos = pos;
    this.line = line;
  }

  @Override
  public boolean isFail() {
    return true;
  }

  @Override
  public FailLog getFailLog() {
    return new FailLog(msg, pos, line);
  }

  @Override
  public T getOrElse(T t) {
    return t;
  }

  public <U> Failure<U> fail() {
    return new Failure<U>(msg, pos, line);
  }

  public final String msg;
  public final int pos;
  public final int line;
}
