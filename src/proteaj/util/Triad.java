package proteaj.util;

public class Triad<T1, T2, T3> {
  public Triad(T1 t1, T2 t2, T3 t3) {
    this._1 = t1;
    this._2 = t2;
    this._3 = t3;
  }

  public static <T1, T2, T3> Triad<T1, T2, T3> make (T1 t1, T2 t2, T3 t3) {
    return new Triad<T1, T2, T3>(t1, t2, t3);
  }

  @Override
  public int hashCode() {
    int hash = 43;
    int mul = 37;
    hash = hash * mul + _1.hashCode();
    hash = hash * mul + _2.hashCode();
    hash = hash * mul + _3.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Triad<?, ?, ?>) {
      Triad<?, ?, ?> tr = (Triad<?, ?, ?>)obj;
      return _1.equals(tr._1) && _2.equals(tr._2) && _3.equals(tr._3);
    }
    else return false;
  }

  public final T1 _1;
  public final T2 _2;
  public final T3 _3;
}

