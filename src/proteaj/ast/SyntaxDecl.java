package proteaj.ast;

import java.util.*;

public class SyntaxDecl extends AST {
  public SyntaxDecl(String name, int line) {
    super(line);
    this.modifiers = 0;
    this.name = name;
    this.baseOperators = null;
    this.mixinOperators = new ArrayList<String>();
    this.operators = new ArrayList<OperatorDecl>();
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void setBaseOperators(String ops) {
    this.baseOperators = ops;
  }

  public void addMixinOperators(String ops) {
    this.mixinOperators.add(ops);
  }

  public void addOperator(OperatorDecl operator) {
    operators.add(operator);
  }

  public boolean hasBaseOperators() {
    return baseOperators != null;
  }

  public int getModifiers() {
    return modifiers;
  }

  public String getName() {
    return name;
  }

  public String getBaseOperators() {
    return baseOperators;
  }

  public List<String> getMixinOperators() {
    return mixinOperators;
  }

  public List<OperatorDecl> getOperators() {
    return operators;
  }

  private int modifiers;
  private String name;
  private String baseOperators;
  private List<String> mixinOperators;
  private List<OperatorDecl> operators;
}

