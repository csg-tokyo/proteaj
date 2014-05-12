package proteaj.ir;

import java.util.*;
import javassist.*;

public class IRSyntax {
  public IRSyntax(CtClass actualClass) {
    this.name = actualClass.getName();
    this.baseSyntax = null;
    this.mixinSyntax = new ArrayList<>();
    this.ops = new ArrayList<>();
  }

  public IRSyntax(String name) {
    this.name = name;
    this.baseSyntax = null;
    this.mixinSyntax = new ArrayList<>();
    this.ops = new ArrayList<>();
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

  public final String name;

  private String baseSyntax;
  private List<String> mixinSyntax;
  private List<IROperator> ops;
}
