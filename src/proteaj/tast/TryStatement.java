package proteaj.tast;

import proteaj.tast.util.*;
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

  public List<Triad<CtClass, String, Block>> getCatchBlocks() { return catchBlocks; }
  public Block getFinallyBlock() { return finallyBlock; }

  public boolean hasCatchBlock() { return ! catchBlocks.isEmpty(); }
  public boolean hasFinallyBlock() { return finallyBlock != null; }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Block tryBlock;
  private List<Triad<CtClass, String, Block>> catchBlocks;
  private Block finallyBlock;
}

