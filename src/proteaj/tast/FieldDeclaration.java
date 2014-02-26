package proteaj.tast;

import javassist.CtField;

public class FieldDeclaration {
  public FieldDeclaration(CtField field, FieldBody body) {
    this.field = field;
    this.body = body;
  }

  public final CtField field;
  public final FieldBody body;
}
