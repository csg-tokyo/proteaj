package proteaj.env.operator;

import proteaj.error.*;
import proteaj.ir.IRSyntax;
import proteaj.ir.OperatorPool;

import java.util.*;

public class RootOperatorEnvironment extends OperatorEnvironment {
  public static RootOperatorEnvironment getInstance() {
    if (instance == null) instance = new RootOperatorEnvironment();
    return instance;
  }

  public IRSyntax getSyntaxOrNull (String name) {
    if (pool.containsSyntax(name)) return pool.getSyntax(name);

    try { return pool.loadOperatorsFile(name); } catch (FileIOError e) {
      ErrorList.addError(e);
      return null;
    }
  }

  @Override
  protected List<IRSyntax> getSyntaxList() {
    if (syntaxList == null) {
      syntaxList = new ArrayList<>();
      addSyntaxToSyntaxList("proteaj.lang.PrimitiveOperators");
      addSyntaxToSyntaxList("proteaj.lang.PrimitiveReadasOperators");
      addSyntaxToSyntaxList("proteaj.lang.StringOperators");
    }
    return syntaxList;
  }

  @Override
  protected Set<IRSyntax> getExcludingSyntax() {
    return Collections.emptySet();
  }

  @Override
  protected NotFoundError makeError(String name) {
    return new NotFoundError(name + " is not found", "(no source)", 0);
  }

  private void addSyntaxToSyntaxList (String name) {
    IRSyntax syntax = getSyntaxOrNull(name);
    if (syntax != null) syntaxList.add(syntax);
  }

  private RootOperatorEnvironment() {
    pool = new OperatorPool();
    pool.loadPrimitiveOperators();
  }

  private final OperatorPool pool;

  private List<IRSyntax> syntaxList = null;

  private static RootOperatorEnvironment instance = null;
}
