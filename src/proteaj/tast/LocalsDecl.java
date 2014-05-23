package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;
import javassist.*;

public class LocalsDecl extends Expression {
  public LocalsDecl(CtClass type, List<LocalDecl> locals) {
    super(CtClass.voidType);
    this.type = type;
    this.locals = locals;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final CtClass type;
  public final List<LocalDecl> locals;

  public static class LocalDecl {
    public LocalDecl (String name, int dim) {
      this.name = name;
      this.dim = dim;
      this.val = null;
    }

    public LocalDecl (String name, int dim, Expression val) {
      this.name = name;
      this.dim = 0;
      this.val = val;
    }

    public final String name;
    public final int dim;
    public final Expression val;
  }
}
