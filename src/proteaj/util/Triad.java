package proteaj.util;

public class Triad<T1, T2, T3> {
  public Triad(T1 t1, T2 t2, T3 t3) {
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
  }

  public T1 getFirst() {
    return t1;
  }

  public T2 getSecond() {
    return t2;
  }

  public T3 getThird() {
    return t3;
  }

  @Override
  public int hashCode() {
    int hash = 43;
    int mul = 37;
    hash = hash * mul + t1.hashCode();
    hash = hash * mul + t2.hashCode();
    hash = hash * mul + t3.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Triad<?, ?, ?>) {
      Triad<?, ?, ?> tr = (Triad<?, ?, ?>)obj;
      return t1.equals(tr.t1) && t2.equals(tr.t2) && t3.equals(tr.t3);
    }
    else return false;
  }

  private T1 t1;
  private T2 t2;
  private T3 t3;
}

