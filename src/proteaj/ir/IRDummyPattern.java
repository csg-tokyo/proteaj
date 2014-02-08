package proteaj.ir;

import proteaj.util.*;

import javassist.*;

public class IRDummyPattern extends IRPattern {
  public static IRDummyPattern getDummy_Readas() {
    if(dummy_readas == null) dummy_readas = new IRDummyPattern(Modifiers.PUBLIC | Modifiers.STATIC | Modifiers.NON_ASSOC | Modifiers.READAS);
    return dummy_readas;
  }

  @Override
  public int hashCode() {
    int hash = 43;
    int mul = 37;
    hash = hash * mul + modifier;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof IRDummyPattern) {
      IRDummyPattern pd = (IRDummyPattern)obj;
      return modifier == pd.modifier;
    }
    else return false;
  }

  @Override
  public boolean isDummy() {
    return true;
  }

  private IRDummyPattern(int mod) {
    super(mod, new int[0], new CtClass[0], new IROperandAttribute[0], new String[0], new CtClass[0], new CtClass[0]);
  }

  private static IRDummyPattern dummy_readas = null;
}

