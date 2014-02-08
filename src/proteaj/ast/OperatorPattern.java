package proteaj.ast;

import java.util.*;

public class OperatorPattern extends AST {
  public OperatorPattern(int line) {
    super(line);
    this.pattern = new ArrayList<OperatorPatternElement>();
  }

  public void append(OperatorPatternElement elem) {
    pattern.add(elem);
  }

  public OperatorPatternElement get(int i) {
    return pattern.get(i);
  }

  public int getLength() {
    return pattern.size();
  }

  public int getOperandLength() {
    int i = 0;
    for(OperatorPatternElement elem : pattern) {
      if(elem.isOperand()) i++;
    }
    return i;
  }

  public int getOperatorLength() {
    int i = 0;
    for(OperatorPatternElement elem : pattern) {
      if(elem.isOperator()) i++;
    }
    return i;
  }

  public int getAndPredicateLength() {
    int i = 0;
    for(OperatorPatternElement elem : pattern) {
      if(elem.isAndPredicate()) i++;
    }
    return i;
  }

  public int getNotPredicateLength() {
    int i = 0;
    for(OperatorPatternElement elem : pattern) {
      if(elem.isNotPredicate()) i++;
    }
    return i;
  }

  public boolean isOperand(int i) {
    return pattern.get(i).isOperand();
  }

  public boolean isOperator(int i) {
    return pattern.get(i).isOperator();
  }

  public boolean isAndPredicate(int i) {
    return pattern.get(i).isAndPredicate();
  }

  public boolean isNotPredicate(int i) {
    return pattern.get(i).isNotPredicate();
  }

  public String getOperator(int i) {
    assert isOperator(i);
    return pattern.get(i).getName();
  }

  public String getPredicateTypeName(int i) {
    assert isAndPredicate(i) || isNotPredicate(i);
    return pattern.get(i).getName();
  }

  private List<OperatorPatternElement> pattern;
}

