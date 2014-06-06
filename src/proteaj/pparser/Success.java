package proteaj.pparser;

public class Success<T> extends ParseResult<T> {
  public Success(T value) {
    this.value = value;
  }

  @Override
  public boolean isFail() {
    return false;
  }

  @Override
  public T getOrElse(T t) {
    return value;
  }

  private final T value;
}
