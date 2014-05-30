package proteaj.pparser;

import proteaj.error.FailLog;

public abstract class ParseResult<T> {
  public abstract boolean isFail();
  public abstract FailLog getFailLog();
  public abstract T getOrElse(T t);

  public T get() {
    assert ! isFail();
    return getOrElse(null);
  }
}

