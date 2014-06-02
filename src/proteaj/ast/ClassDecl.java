package proteaj.ast;

import java.util.*;

public class ClassDecl extends AST {
  public ClassDecl(String name, int line) {
    super(line);
    this.modifiers = 0;
    this.name = name;
    this.superClass = null;
    this.interfaces = new ArrayList<String>();
    this.sinits = new HashSet<StaticInitializer>();
    this.constructors = new HashSet<ConstructorDecl>();
    this.methods = new HashSet<MethodDecl>();
    this.fields = new ArrayList<FieldDecl>();
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void setSuperClass(String superClass) {
    this.superClass = superClass;
  }

  public void addInterface(String iface) {
    this.interfaces.add(iface);
  }

  public void addStaticInitializer(StaticInitializer sinit) {
    this.sinits.add(sinit);
  }

  public void addConstructor(ConstructorDecl constructor) {
    this.constructors.add(constructor);
  }

  public void addMethod(MethodDecl method) {
    this.methods.add(method);
  }

  public void addField(FieldDecl field) {
    this.fields.add(field);
  }

  public int getModifiers() {
    return modifiers;
  }

  public String getName() {
    return name;
  }

  public String getSuperClass() {
    if(superClass == null) return "java.lang.Object";
    else return superClass;
  }

  public List<String> getInterfaces() {
    return interfaces;
  }

  public Set<StaticInitializer> getStaticInitializers() {
    return sinits;
  }

  public Set<ConstructorDecl> getConstructors() {
    return constructors;
  }

  public Set<MethodDecl> getMethods() {
    return methods;
  }

  public List<FieldDecl> getFields() {
    return fields;
  }

  private int modifiers;
  private String name;
  private String superClass;
  private List<String> interfaces;
  private Set<StaticInitializer> sinits;
  private Set<ConstructorDecl> constructors;
  private Set<MethodDecl> methods;
  private List<FieldDecl> fields;
}

