package proteaj.ir;

import proteaj.env.type.TypeResolver;

import java.util.*;

public class IRHeader {
  public IRHeader(String filePath, String packageName, List<String> importPackages, List<String> importClasses, List<String> usingSyntax, Set<String> unusingSyntax) {
    this.filePath = filePath;
    this.packageName = packageName;
    this.importPackages = importPackages;
    this.importClasses = importClasses;
    this.usingSyntax = usingSyntax;
    this.unusingSyntax = unusingSyntax;

    this.resolver = TypeResolver.onFile(filePath, packageName, importPackages, importClasses);

    this.usingSyntax.add(0, "proteaj.lang.PrimitiveOperators");
    this.usingSyntax.add(1, "proteaj.lang.PrimitiveReadasOperators");
    this.usingSyntax.add(2, "proteaj.lang.StringOperators");
  }

  public final String filePath;
  public final String packageName;
  public final List<String> importPackages;
  public final List<String> importClasses;
  public final List<String> usingSyntax;
  public final Set<String> unusingSyntax;

  public final TypeResolver resolver;
}

