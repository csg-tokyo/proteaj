package proteaj.ir.tast;

import javassist.*;

import static proteaj.util.Escape.*;

public class CharLiteral extends Expression {
  public CharLiteral(char val) {
    super(CtClass.charType);
    this.val = val;
  }

  public char getValue() {
    return val;
  }

  @Override
  public String toJavassistCode() {
    return "\'" + escape(val) + "\'";
  }

  private char val;
}
