package proteaj.util;

public class Quad<T1, T2, T3, T4> {
  public Quad(T1 t1, T2 t2, T3 t3, T4 t4) {
    this._1 = t1;
    this._2 = t2;
    this._3 = t3;
    this._4 = t4;
  }

  public static <T1, T2, T3, T4> Quad<T1, T2, T3, T4> make (T1 t1, T2 t2, T3 t3, T4 t4) {
    return new Quad<T1, T2, T3, T4>(t1, t2, t3, t4);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Quad quad = (Quad) o;

    if (!_1.equals(quad._1)) return false;
    if (!_2.equals(quad._2)) return false;
    if (!_3.equals(quad._3)) return false;
    if (!_4.equals(quad._4)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = _1.hashCode();
    result = 31 * result + _2.hashCode();
    result = 31 * result + _3.hashCode();
    result = 31 * result + _4.hashCode();
    return result;
  }

  public final T1 _1;
  public final T2 _2;
  public final T3 _3;
  public final T4 _4;
}
