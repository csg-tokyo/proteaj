package proteaj.ir;

import java.util.*;

public class IRSyntax {
  public IRSyntax(String name) {
    this.name = name;
    this.baseSyntax = null;
    this.mixinSyntax = new ArrayList<String>();
    this.ops = new ArrayList<IROperator>();
  }

  public void setBaseSyntax(String baseSyntax) {
    this.baseSyntax = baseSyntax;
  }

  public void addMixinSyntax(String mixinSyntax) {
    this.mixinSyntax.add(mixinSyntax);
  }

  public void addOperator(IROperator odata) {
    ops.add(odata);
  }

  public boolean hasBaseSyntax() {
    return baseSyntax != null;
  }

  public String getName() {
    return name;
  }

  public String getBaseSyntax() {
    return baseSyntax;
  }

  public List<String> getMixinSyntax() {
    return mixinSyntax;
  }

  public List<IROperator> getOperators() {
    return ops;
  }

  private String name;
  private String baseSyntax;
  private List<String> mixinSyntax;
  private List<IROperator> ops;
}
