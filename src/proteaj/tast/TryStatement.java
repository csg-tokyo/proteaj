package proteaj.tast;

import proteaj.tast.util.*;

import java.util.*;

public class TryStatement extends Statement {
  public TryStatement(Block tryBlock) {
    this.tryBlock = tryBlock;
    this.catchBlocks = new ArrayList<>();
    this.finallyBlock = null;
  }

  public TryStatement(Block tryBlock, Block finallyBlock) {
    this.tryBlock = tryBlock;
    this.catchBlocks = new ArrayList<>();
    this.finallyBlock = finallyBlock;
  }

  public TryStatement(Block tryBlock, List<CatchBlock> catchBlocks) {
    this.tryBlock = tryBlock;
    this.catchBlocks = catchBlocks;
    this.finallyBlock = null;
  }

  public TryStatement(Block tryBlock, List<CatchBlock> catchBlocks, Block finallyBlock) {
    this.tryBlock = tryBlock;
    this.catchBlocks = catchBlocks;
    this.finallyBlock = finallyBlock;
  }

  public Block getFinallyBlock() { return finallyBlock; }

  public boolean hasFinallyBlock() { return finallyBlock != null; }

  @Override
  public <T> T accept(StatementVisitor<T> visitor, T t) {
    return visitor.visit(this, t);
  }

  public final Block tryBlock;
  public final List<CatchBlock> catchBlocks;
  private final Block finallyBlock;
}

