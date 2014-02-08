package proteaj.ir;

import proteaj.ir.primitive.*;

import java.util.*;

public class OperatorPool {
  public OperatorPool() {
    syntax = new HashMap<String, IRSyntax>();
  }

  public void loadPrimitiveOperators() {
    syntax.put("proteaj.lang.PrimitiveOperators", PrimitiveSyntax.getSyntax());
    syntax.put("proteaj.lang.PrimitiveReadasOperators", PrimitiveReadasSyntax.getSyntax());
  }

  public void addSyntax(IRSyntax syn) {
    syntax.put(syn.getName(), syn);
  }

  public boolean containsSyntax(String sysName) {
    return syntax.containsKey(sysName);
  }

  public IRSyntax getSyntax(String synName) {
    return syntax.get(synName);
  }

  private Map<String, IRSyntax> syntax;
}
