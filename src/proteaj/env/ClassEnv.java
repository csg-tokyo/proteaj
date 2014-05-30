package proteaj.env;

import proteaj.env.operator.OperatorEnvironment;
import proteaj.env.type.TypeResolver;

import javassist.*;

public class ClassEnv {
  public ClassEnv(OperatorEnvironment operatorEnv, TypeResolver resolver, CtClass thisClass) {
    this.operatorEnv = operatorEnv;
    this.resolver = resolver;
    this.thisClass = thisClass;
  }

  public final OperatorEnvironment operatorEnv;
  public final TypeResolver resolver;
  public final CtClass thisClass;
}
