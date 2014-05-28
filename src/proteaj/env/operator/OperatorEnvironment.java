package proteaj.env.operator;

import proteaj.ir.*;

import java.util.*;
import javassist.*;

public abstract class OperatorEnvironment {
  public NavigableMap<Integer, List<IROperator>> getOperators (CtClass clazz) {
    initialize();
    if (normalMap.containsKey(clazz)) return normalMap.get(clazz);
    else return Collections.emptyNavigableMap();
  }

  public NavigableMap<Integer, List<IROperator>> getReadAsOperators (CtClass clazz) {
    initialize();
    if (readAsMap.containsKey(clazz)) return readAsMap.get(clazz);
    else return Collections.emptyNavigableMap();
  }

  protected abstract List<IRSyntax> getSyntaxList();
  protected abstract Set<IRSyntax> getExcludingSyntax();

  private void initialize () {
    if (normalMap == null || readAsMap == null) {
      int basePriority = 0;
      for (IRSyntax syntax : getSyntaxList()) {
        basePriority = loadSyntax(syntax, basePriority) + 1;
      }
    }
  }

  // under construction
  private int loadSyntax (IRSyntax syntax, int basePriority) {
    if (getExcludingSyntax().contains(syntax)) return basePriority;

    return basePriority;
  }

  private Map<CtClass, TreeMap<Integer, List<IROperator>>> normalMap = null;
  private Map<CtClass, TreeMap<Integer, List<IROperator>>> readAsMap = null;
}
