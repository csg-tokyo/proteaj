package proteaj.tast;

import javassist.CtMethod;

public class DefaultValueDefinition {
  public DefaultValueDefinition(CtMethod method, DefaultValue body) {
    this.method = method;
    this.body = body;
  }

  public final CtMethod method;
  public final DefaultValue body;
}
