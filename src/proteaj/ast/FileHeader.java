package proteaj.ast;

import java.util.*;

public class FileHeader extends AST {
  public FileHeader(int line) {
    super(line);
    this.packName = null;
    this.importPack = new ArrayList<String>();
    this.importCls = new ArrayList<String>();
    this.usingSyn = new ArrayList<String>();
    this.unusingSyn = new HashSet<String>();
  }

  public void setPackageName(String pack) {
    packName = pack;
  }

  public void addImportPackage(String pack) {
    importPack.add(pack);
  }

  public void addImportClass(String cls) {
    importCls.add(cls);
  }

  public void addUsingSyntax(String syn) {
    usingSyn.add(syn);
  }

  public void addUnusingSyntax(String syn) {
    unusingSyn.add(syn);
  }

  public String getPackName() {
    if(packName == null) return "";
    else return packName;
  }

  public List<String> getImportPackages() {
    return importPack;
  }

  public List<String> getImportClasses() {
    return importCls;
  }

  public List<String> getUsingSyntax() {
    return usingSyn;
  }

  public Set<String> getUnusingSyntax() {
    return unusingSyn;
  }

  private String packName;
  private List<String> importPack;
  private List<String> importCls;
  private List<String> usingSyn;
  private Set<String> unusingSyn;
}

