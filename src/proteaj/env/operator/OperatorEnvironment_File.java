package proteaj.env.operator;

import proteaj.error.NotFoundError;
import proteaj.ir.IRSyntax;

import java.util.*;

class OperatorEnvironment_File extends OperatorEnvironment {

  public OperatorEnvironment_File (String fileName, List<String> usingSyntax, Set<String> unusingSyntax) {
    this.fileName = fileName;
    this.usingSyntax = usingSyntax;
    this.unusingSyntax = unusingSyntax;
    this.root = root();
  }

  @Override
  protected List<IRSyntax> getSyntaxList() {
    if (usingList == null) {
      usingList = new ArrayList<>(root.getSyntaxList());
      for (String name : usingSyntax) {
        IRSyntax syntax = root.getSyntaxOrNull(name);
        if (syntax != null) usingList.add(syntax);
      }
    }
    return usingList;
  }

  @Override
  protected Set<IRSyntax> getExcludingSyntax() {
    if (unusingSet == null) {
      unusingSet = new HashSet<>();
      for (String name : unusingSyntax) {
        IRSyntax syntax = root.getSyntaxOrNull(name);
        if (syntax != null) unusingSet.add(syntax);
      }
    }
    return unusingSet;
  }

  @Override
  protected NotFoundError makeError(String name) {
    return new NotFoundError(name + " is not found", fileName, 0);
  }

  public final String fileName;
  public final List<String> usingSyntax;
  public final Set<String> unusingSyntax;
  public final RootOperatorEnvironment root;

  private List<IRSyntax> usingList = null;
  private Set<IRSyntax> unusingSet = null;
}
