package proteaj.ir.tast;

import proteaj.util.*;

import java.util.*;
import javassist.*;

public class TryStatement extends Statement {
  public TryStatement(Statement tryBlock) {
    this.tryBlock = tryBlock;
    this.catchBlocks = new ArrayList<Triad<CtClass,String,Statement>>();
    this.finallyBlock = null;
  }

  public void addCatchBlock(CtClass type, String name, Statement block) {
    catchBlocks.add(new Triad<CtClass, String, Statement>(type, name, block));
  }

  public void setFinallyBlock(Statement finallyBlock) {
    this.finallyBlock = finallyBlock;
  }

  public boolean hasCatchBlock() {
    return ! catchBlocks.isEmpty();
  }

  @Override
  public String toJavassistCode() {
    StringBuilder buf = new StringBuilder();

    buf.append("try ").append(tryBlock.toJavassistCode());
    for(Triad<CtClass, String, Statement> c : catchBlocks) {
      buf.append(" catch (").append(c.getFirst().getName()).append(' ').append(c.getSecond()).append(") ").append(c.getThird().toJavassistCode());
    }
    if(finallyBlock != null) buf.append(" finally ").append(finallyBlock.toJavassistCode());

    return buf.toString();
  }

  private Statement tryBlock;
  private List<Triad<CtClass, String, Statement>> catchBlocks;
  private Statement finallyBlock;
}

