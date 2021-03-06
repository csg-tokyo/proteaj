package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class LocalsDecl extends Expression {
  public LocalsDecl(boolean isFinal, CtClass type, List<LocalDecl> locals) {
    super(CtClass.voidType);
    this.isFinal = isFinal;
    this.type = type;
    this.locals = locals;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final boolean isFinal;
  public final CtClass type;
  public final List<LocalDecl> locals;

  public static class LocalDecl {
    public LocalDecl (CtClass type, String name, int dim) {
      this.type = type;
      this.name = name;
      this.dim = dim;
      this.val = null;
    }

    public LocalDecl (CtClass type, String name, int dim, Expression val) {
      this.type = type;
      this.name = name;
      this.dim = dim;
      this.val = val;
    }

    public final CtClass type;
    public final String name;
    public final int dim;
    public final Expression val;
  }
}
