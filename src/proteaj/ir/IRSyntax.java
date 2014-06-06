package proteaj.ir;

import java.util.*;
import javassist.*;

public class IRSyntax {
  public IRSyntax(CtClass actualClass, String filePath) {
    this.name = actualClass.getName();
    this.filePath = filePath;
    this.baseIRSyntax = null;
    this.ops = new ArrayList<>();
  }

  public IRSyntax(String name, String filePath) {
    this.name = name;
    this.filePath = filePath;
    this.baseIRSyntax = null;
    this.ops = new ArrayList<>();
  }

  public void setBaseIRSyntax (IRSyntax syntax) { this.baseIRSyntax = syntax; }

  public void addOperator(IROperator odata) {
    ops.add(odata);
  }

  public void addOperators (IROperator... operators) { ops.addAll(Arrays.asList(operators)); }

  public boolean hasBaseIRSyntax() { return baseIRSyntax != null; }

  public IRSyntax getBaseIRSyntax() { return baseIRSyntax; }

  public List<IROperator> getOperators() {
    return ops;
  }

  public final String name;
  public final String filePath;

  private IRSyntax baseIRSyntax;

  private List<IROperator> ops;
}
