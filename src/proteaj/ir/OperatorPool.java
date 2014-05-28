package proteaj.ir;

import proteaj.error.FileIOError;
import proteaj.io.OperatorsFile;
import proteaj.ir.primitive.*;

import java.util.*;

public class OperatorPool {
  public OperatorPool() {
    syntax = new HashMap<>();
  }

  public void loadPrimitiveOperators() {
    syntax.put("proteaj.lang.PrimitiveOperators", PrimitiveSyntax.getSyntax());
    syntax.put("proteaj.lang.PrimitiveReadasOperators", PrimitiveReadasSyntax.getSyntax());
  }

  public void addSyntax(IRSyntax syn) {
    syntax.put(syn.name, syn);
  }

  public boolean containsSyntax(String sysName) {
    return syntax.containsKey(sysName);
  }

  public IRSyntax getSyntax(String synName) {
    return syntax.get(synName);
  }



  public IRSyntax loadOperatorsFile (String name) throws FileIOError {
    if (containsSyntax(name)) return getSyntax(name);

    OperatorsFile file = OperatorsFile.loadOperatorsFile(name);
    if (file == null) return null;

    IRSyntax syntax = file.read(this);
    if (syntax == null) return null;

    addSyntax(syntax);
    return syntax;
  }

  private Map<String, IRSyntax> syntax;
}
