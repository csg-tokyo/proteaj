package proteaj.util;


public class Pair<T1, T2> {
  public Pair(T1 t1, T2 t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  public T1 getFirst() {
    return t1;
  }

  public T2 getSecond() {
    return t2;
  }

  @Override
  public int hashCode() {
    int t1hash = t1 == null ? 0 : t1.hashCode();
    int t2hash = t2 == null ? 0 : t2.hashCode();

    return t1hash * 37 + t2hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Pair<?, ?>) {
      Pair<?, ?> that = (Pair<?, ?>)obj;
      return (t1 == null ? that.t1 == null : t1.equals(that.t1)) && (t2 == null ? that.t2 == null : t2.equals(that.t2));
    }
    return false;
  }

  private T1 t1;
  private T2 t2;
}

