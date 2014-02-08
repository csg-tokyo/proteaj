package proteaj.ast;

import java.util.*;

public class FileBody extends AST {
  public FileBody(int line) {
    super(line);
    classes = new HashSet<ClassDecl>();
    interfaces = new HashSet<InterfaceDecl>();
    syntax = new HashSet<SyntaxDecl>();
  }

  public void addClass(ClassDecl cdecl) {
    classes.add(cdecl);
  }

  public void addInterface(InterfaceDecl idecl) {
    interfaces.add(idecl);
  }

  public void addSyntax(SyntaxDecl syndecl) {
    syntax.add(syndecl);
  }

  public Set<ClassDecl> getClasses() {
    return classes;
  }

  public Set<InterfaceDecl> getInterfaces() {
    return interfaces;
  }

  public Set<SyntaxDecl> getSyntax() {
    return syntax;
  }

  private Set<ClassDecl> classes;
  private Set<InterfaceDecl> interfaces;
  private Set<SyntaxDecl> syntax;
}

