package proteaj.ir;

import proteaj.util.Modifiers;
import proteaj.ast.OperatorPattern;

import javassist.*;
import static proteaj.util.Modifiers.isNonAssoc;
import static proteaj.util.Modifiers.isRightAssoc;

public class IRPattern {
  public IRPattern(int modifier, OperatorPattern oppat, CtClass[] paramTypes, IROperandAttribute[] paramMods, CtClass[] andPreds, CtClass[] notPreds) {
    this.modifier = modifier;
    this.paramTypes = paramTypes;
    this.paramMods = paramMods;

    int length = oppat.getLength();
    this.pattern = new int[length];
    this.keywords = new String[oppat.getOperatorLength()];
    this.andPreds = andPreds;
    this.notPreds = notPreds;

    int nOperand = 0;
    int nOperator = 0;
    int nAndPred = 0;
    int nNotPred = 0;
    for(int i = 0; i < length; i++) {
      if(oppat.isOperand(i)) {
        pattern[i] = nOperand | PATTERN_OPERAND;
        nOperand++;
      }
      else if(oppat.isOperator(i)) {
        pattern[i] = nOperator | PATTERN_OPERATOR;
        keywords[nOperator] = oppat.getOperator(i);
        nOperator++;
      }
      else if(oppat.isAndPredicate(i)) {
        pattern[i] = nAndPred | PATTERN_ANDPRED;
        nAndPred++;
      }
      else if(oppat.isNotPredicate(i)) {
        pattern[i] = nNotPred | PATTERN_NOTPRED;
        nNotPred++;
      }
    }
  }

  public IRPattern(int modifier, int[] patternIds, CtClass[] paramTypes, IROperandAttribute[] paramMods, String[] keywords, CtClass[] andPreds, CtClass[] notPreds) {
    this.modifier = modifier;
    this.pattern = patternIds;
    this.paramTypes = paramTypes;
    this.paramMods = paramMods;
    this.keywords = keywords;
    this.andPreds = andPreds;
    this.notPreds = notPreds;
  }

  @Override
  public int hashCode() {
    int hash = 43;
    int mul = 37;
    hash = hash * mul + modifier;
    hash = hash * mul + pattern.hashCode();
    hash = hash * mul + paramTypes.hashCode();
    hash = hash * mul + keywords.hashCode();
    hash = hash * mul + andPreds.hashCode();
    hash = hash * mul + notPreds.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof IRPattern) {
      IRPattern pd = (IRPattern)obj;
      return modifier == pd.modifier && pattern.equals(pd.pattern) && paramTypes.equals(pd.paramTypes) && keywords.equals(pd.keywords) && andPreds.equals(pd.andPreds) && notPreds.equals(notPreds);
    }
    else return false;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();

    int size = getPatternLength();
    for(int i = 0; i < size; i++) {
      if(buf.length() != 0) buf.append(' ');
      if(isOperand(i)) {
        buf.append('<').append(getOperandType(i).getName()).append('>');
      }
      else if(isOperator(i)) {
        buf.append(getOperatorKeyword(i));
      }
      else if(isAndPredicate(i)) {
        buf.append('&').append(getAndPredicateType(i).getName());
      }
      else if(isNotPredicate(i)) {
        buf.append('!').append(getNotPredicateType(i).getName());
      }
    }

    return buf.toString();
  }

  public boolean isDummy() {
    return false;
  }

  public int getModifier() {
    return modifier;
  }

  public boolean isLAssoc() {
    return ! isRightAssoc(modifier) && ! isNonAssoc(modifier);
  }

  public boolean isRAssoc() {
    return isRightAssoc(modifier);
  }

  public boolean isReadas() {
    return Modifiers.isReadas(modifier);
  }

  public int getPatternLength() {
    return pattern.length;
  }

  public int getOperatorsLength() {
    return keywords.length;
  }

  public int getOperandsLength() {
    return paramTypes.length;
  }

  public int getAndPredicatesLength() {
    return andPreds.length;
  }

  public int getNotPredicatesLength() {
    return notPreds.length;
  }

  public boolean isOperand(int i) {
    assert 0 <= i && i < getPatternLength();
    return (pattern[i] & PATTERN_OPERAND) == PATTERN_OPERAND;
  }

  public boolean isOperator(int i) {
    assert 0 <= i && i < pattern.length;
    return (pattern[i] & PATTERN_OPERATOR) == PATTERN_OPERATOR;
  }

  public boolean isAndPredicate(int i) {
    assert 0 <= i && i < getPatternLength();
    return (pattern[i] & PATTERN_ANDPRED) == PATTERN_ANDPRED;
  }

  public boolean isNotPredicate(int i) {
    assert 0 <= i && i < pattern.length;
    return (pattern[i] & PATTERN_NOTPRED) == PATTERN_NOTPRED;
  }

  public boolean isOptionOperand(int i) {
    return getOperandModifier(i).isOption();
  }

  public boolean isVariableOperands(int i) {
    return getOperandModifier(i).hasVarArgs();
  }

  public boolean hasMoreThanOneOperands(int i) {
    return getOperandModifier(i).hasMoreThanOneArgs();
  }

  public boolean hasSeparator(int i) {
    return getOperandModifier(i).hasSeparator();
  }

  public String getSeparator(int i) {
    return getOperandModifier(i).getSeparator();
  }

  public CtMethod getDefaultMethod(int i) {
    return getOperandModifier(i).getDefaultMethod();
  }

  public IROperandAttribute getOperandModifier(int i) {
    assert isOperand(i);
    return paramMods[getOperandIndex(i)];
  }

  public boolean getInclusive(int i) {
    assert isOperand(i);
    if(isLAssoc()) return i != getPatternLength() - 1;
    else if(isRAssoc()) return i != 0;
    else return false;
  }

  public CtClass getOperandType(int i) {
    assert isOperand(i);
    return paramTypes[getOperandIndex(i)];
  }

  public String getOperatorKeyword(int i) {
    assert isOperator(i);
    return keywords[getOperatorIndex(i)];
  }

  public CtClass getAndPredicateType(int i) {
    assert isAndPredicate(i);
    return andPreds[getAndPredicateIndex(i)];
  }

  public CtClass getNotPredicateType(int i) {
    assert isNotPredicate(i);
    return notPreds[getNotPredicateIndex(i)];
  }

  public int getPatternId(int i) {
    return pattern[i];
  }

  private int getOperandIndex(int i) {
    return getPatternId(i) & ~PATTERN_OPERAND;
  }

  private int getOperatorIndex(int i) {
    return getPatternId(i) & ~PATTERN_OPERATOR;
  }

  private int getAndPredicateIndex(int i) {
    return getPatternId(i) & ~PATTERN_ANDPRED;
  }

  private int getNotPredicateIndex(int i) {
    return getPatternId(i) & ~PATTERN_NOTPRED;
  }

  protected int modifier;
  private int[] pattern;
  private CtClass[] paramTypes;
  private IROperandAttribute[] paramMods;
  private String[] keywords;
  private CtClass[] andPreds;
  private CtClass[] notPreds;

  public static final int PATTERN_OPERAND  = 0x10000;
  public static final int PATTERN_OPERATOR = 0x20000;
  public static final int PATTERN_ANDPRED  = 0x40000;
  public static final int PATTERN_NOTPRED  = 0x80000;
}

