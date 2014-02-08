package proteaj.ir;

import java.util.*;

public class IRHeader {
  public IRHeader(String filePath, String packageName, List<String> importPackages, List<String> usingSyntax, Set<String> unusingSyntax) {
    this.filePath = filePath;
    this.packageName = packageName;
    this.importPackages = importPackages;
    this.usingSyntax = usingSyntax;
    this.unusingSyntax = unusingSyntax;
    this.typeNameMap = new HashMap<String, String>();

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

  public String getFilePath() {
    return filePath;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<String> getImportPackages() {
    return importPackages;
  }

  public List<String> getUsingSyntax() {
    return usingSyntax;
  }

  public Set<String> getUnusingSyntax() {
    return unusingSyntax;
  }

  private String filePath;
  private String packageName;
  private List<String> importPackages;
  private List<String> usingSyntax;
  private Set<String> unusingSyntax;
  private Map<String, String> typeNameMap;
}

