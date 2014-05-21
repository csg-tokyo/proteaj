package proteaj.ir;

import proteaj.ast.OperatorPattern;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class IROperator {
  public IROperator(int modifier, CtClass returnType, List<CtClass> returnTypeBounds, OperatorPattern oppat, CtClass[] paramTypes, IROperandAttribute[] paramMods, CtClass[] andPreds, CtClass[] notPreds, int priority, CtMethod actualMethod) {
    this.returnType = returnType;
    this.returnTypeBounds = returnTypeBounds;
    this.priority = priority;
    this.pattern = new IRPattern(modifier, oppat, paramTypes, paramMods, andPreds, notPreds);
    this.declCls = actualMethod.getDeclaringClass();
    this.actualMethod = actualMethod;
  }

  public IROperator(CtClass returnType, List<CtClass> returnTypeBounds, IRPattern pattern, int priority, CtClass clz, CtMethod method) {
    this.returnType = returnType;
    this.returnTypeBounds = returnTypeBounds;
    this.pattern = pattern;
    this.priority = priority;
    this.declCls = clz;
    this.actualMethod = method;
  }

  protected IROperator(CtClass returnType, List<CtClass> returnTypeBounds, IRPattern pattern, int priority) {
    this.returnType = returnType;
    this.returnTypeBounds = returnTypeBounds;
    this.pattern = pattern;
    this.priority = priority;
    this.declCls = null;
    this.actualMethod = null;
  }

  public String getClassName() {
    if(declCls != null) return declCls.getName();
    else return null;
  }

  public String getMethodName() {
    if(actualMethod != null) return actualMethod.getName();
    else return null;
  }

  public CtClass[] getExceptionTypes() throws NotFoundException {
    if(actualMethod != null) return actualMethod.getExceptionTypes();
    else return new CtClass[0];
  }

  public String toString(List<Expression> operands) {
    StringBuilder buf = new StringBuilder();

    for(int i = 0, j = 0; i < pattern.getPatternLength(); i++) {
      if(pattern.isOperator(i)) buf.append(pattern.getOperatorKeyword(i));
      else if(pattern.isOperand(i)) buf.append(operands.get(j++));

      if(i != pattern.getPatternLength() - 1) buf.append(' ');
    }

    return buf.toString();
  }

  public final int priority;
  public final CtClass returnType;
  public final List<CtClass> returnTypeBounds;
  public final IRPattern pattern;

  private final CtClass declCls;

  public final CtMethod actualMethod;
}

