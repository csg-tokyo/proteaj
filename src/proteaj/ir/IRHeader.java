package proteaj.ir;

import javassist.CtClass;

import java.util.*;

public class IRHeader {
  public IRHeader(String filePath, String packageName, List<String> importPackages, List<String> usingSyntax, Set<String> unusingSyntax) {
    this.filePath = filePath;
    this.packageName = packageName;
    this.importPackages = importPackages;
    this.usingSyntax = usingSyntax;
    this.unusingSyntax = unusingSyntax;
    this.typeNameMap = new HashMap<>();

    this.importPackages.add(0, "java.lang");
    this.importPackages.add(1, "proteaj.lang");
    this.usingSyntax.add(0, "proteaj.lang.PrimitiveOperators");
    this.usingSyntax.add(1, "proteaj.lang.PrimitiveReadasOperators");
    this.usingSyntax.add(2, "proteaj.lang.StringOperators");
  }

  public void addAbbName(String shortName, String longName) {
    typeNameMap.put(shortName, longName);
  }

  public boolean containsAbbName(String abbName) {
    return typeNameMap.containsKey(abbName);
  }

  public String getLongName(String shortName) {
    return typeNameMap.get(shortName);
  }

  public final String filePath;
  public final String packageName;
  public final List<String> importPackages;
  public final List<String> usingSyntax;
  public final Set<String> unusingSyntax;

  private Map<String, String> typeNameMap;
}

