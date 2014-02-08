package proteaj.ast;

import java.util.*;

public class InterfaceDecl extends AST {
  public InterfaceDecl(String name, int line) {
    super(line);
    this.modifiers = 0;
    this.name = name;
    this.interfaces = new ArrayList<String>();
    this.methods = new HashSet<MethodDecl>();
    this.fields = new HashSet<FieldDecl>();
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void addInterface(String iface) {
    this.interfaces.add(iface);
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

  public List<String> getInterfaces() {
    return interfaces;
  }

  public Set<MethodDecl> getMethods() {
    return methods;
  }

  public Set<FieldDecl> getFields() {
    return fields;
  }

  private int modifiers;
  private String name;
  private List<String> interfaces;
  private Set<MethodDecl> methods;
  private Set<FieldDecl> fields;
}

