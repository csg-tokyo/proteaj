package proteaj.tast;

import javassist.CtClass;

public class CatchBlock {
  public CatchBlock (CtClass clazz, String name, Block block) {
    this.clazz = clazz;
    this.name = name;
    this.block = block;
  }

  public final CtClass clazz;
  public final String name;
  public final Block block;
}
