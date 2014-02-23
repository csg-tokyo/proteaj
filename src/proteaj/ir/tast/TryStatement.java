package proteaj.ir.tast;

import proteaj.util.*;

import java.util.*;
import javassist.*;

public class TryStatement extends Statement {
  public TryStatement(Block tryBlock) {
    this.tryBlock = tryBlock;
    this.catchBlocks = new ArrayList<Triad<CtClass,String,Block>>();
    this.finallyBlock = null;
  }

  public void addCatchBlock(CtClass type, String name, Block block) {
    catchBlocks.add(new Triad<CtClass, String, Block>(type, name, block));
  }

  public void setFinallyBlock(Block finallyBlock) {
    this.finallyBlock = finallyBlock;
  }

  public boolean hasCatchBlock() {
    return ! catchBlocks.isEmpty();
  }

  @Override
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();

    buf.append("try ").append(tryBlock.toJavassistCode());
    for(Triad<CtClass, String, Block> c : catchBlocks) {
      buf.append(" catch (").append(c.getFirst().getName()).append(' ').append(c.getSecond()).append(") ").append(c.getThird().toJavassistCode());
    }
    if(finallyBlock != null) buf.append(" finally ").append(finallyBlock.toJavassistCode());

    return buf.toString();
  }

  private Block tryBlock;
  private List<Triad<CtClass, String, Block>> catchBlocks;
  private Block finallyBlock;
}

