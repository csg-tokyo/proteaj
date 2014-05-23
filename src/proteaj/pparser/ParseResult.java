package proteaj.pparser;

import proteaj.error.ForDebug;
import proteaj.error.FailLog;
import proteaj.tast.Expression;

public abstract class ParseResult<T> {
  public abstract boolean isFail();
  public abstract FailLog getFailLog();
  public abstract T getOrElse(T t);

  public T get() {
    assert ! isFail();
    return getOrElse(null);
  }
}

class Success<T> extends ParseResult<T> {
  public Success(T value) {
    this.value = value;
    ForDebug.print(value);
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
    return value;
  }

  private final T value;
}

class Failure<T> extends ParseResult<T> {
  public Failure(String msg, int pos, int line) {
    this.msg = msg;
    this.pos = pos;
    this.line = line;
    //ForDebug.print("[ parse fail ] " + "(" + line + "," + pos + ") " + msg);
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
