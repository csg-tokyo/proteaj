package proteaj.pparser;

import proteaj.error.FailLog;

public class Success<T> extends ParseResult<T> {
  public Success(T value) {
    this.value = value;
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
