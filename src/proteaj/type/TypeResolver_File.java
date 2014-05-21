package proteaj.type;

import javassist.CtClass;
import proteaj.error.NotFoundError;

import java.util.List;

class TypeResolver_File extends TypeResolver {
  public TypeResolver_File(String fileName, String packageName, List<String> importPackages, List<String> importClasses) {
    this.fileName = fileName;
    this.packageName = packageName;
    this.importPackages = importPackages;
    this.importClasses = importClasses;
    this.root = getRootResolver();
  }

  @Override
  protected CtClass getTypeNameOrNull_NonCached(String name) {
    if (name == null) return null;

    String prefix = '.' + (name.contains(".") ? name.substring(0, name.indexOf('.')) : name);

    for (String imported : importClasses) {
      if (imported.endsWith(prefix)) {
        return root.searchType(imported.substring(0, imported.lastIndexOf(prefix)), name);
      }
    }

    CtClass clazz = root.searchType(packageName, name);
    if (clazz != null) return clazz;

    for (String imported : importPackages) {
      clazz = root.searchType(imported, name);
      if (clazz != null) return clazz;
    }

    return root.getTypeNameOrNull(name);
  }

  @Override
  protected NotFoundError makeError(String name) {
    return new NotFoundError(name + " is not found", fileName, 0);
  }

  public final String fileName;
  public final String packageName;
  public final List<String> importPackages;
  public final List<String> importClasses;

  private final RootTypeResolver root;
}
