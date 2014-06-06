package proteaj.pparser;

public abstract class ParseResult<T> {
  public abstract boolean isFail();
  public abstract T getOrElse(T t);

  public T get() {
    assert ! isFail();
    return getOrElse(null);
  }
}

