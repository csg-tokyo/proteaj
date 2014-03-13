package proteaj.util;


public class Pair<T1, T2> {
  public Pair(T1 t1, T2 t2) {
    this._1 = t1;
    this._2 = t2;
  }

  public static <T1, T2> Pair<T1, T2> make (T1 t1, T2 t2) {
    return new Pair<T1, T2>(t1, t2);
  }

  @Override
  public int hashCode() {
    int t1hash = _1 == null ? 0 : _1.hashCode();
    int t2hash = _2 == null ? 0 : _2.hashCode();

    return t1hash * 37 + t2hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Pair<?, ?>) {
      Pair<?, ?> that = (Pair<?, ?>)obj;
      return (_1 == null ? that._1 == null : _1.equals(that._1)) && (_2 == null ? that._2 == null : _2.equals(that._2));
    }
    return false;
  }

  public final T1 _1;
  public final T2 _2;
}

