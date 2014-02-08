package proteaj.ir;

import java.util.*;
import javassist.*;

public class IR {
  public IR() {
    classenv = new HashMap<CtClass, IRHeader>();
    methods = new ArrayList<IRMethodBody>();
    constructors = new ArrayList<IRConstructorBody>();
    fields = new ArrayList<IRFieldBody>();
    syntax = new ArrayList<IRSyntax>();
    darguments = new ArrayList<IRDefaultArgument>();
    sinits = new ArrayList<IRStaticInitializer>();

    this.cpool = ClassPool.getDefault();
    this.opool = new OperatorPool();
  }

  public CtClass makeClass(String name, int mods) {
    CtClass ctcl = cpool.makeClass(name);
    ctcl.setModifiers(mods);

    return ctcl;
  }

  public void addClass(CtClass scl, IRHeader hdata) {
    classenv.put(scl, hdata);
  }

  public void addInterface(CtClass iface, IRHeader hdata) {
    classenv.put(iface, hdata);
  }

  public void addMethod(IRMethodBody method) {
    methods.add(method);
  }

  public void addConstructor(IRConstructorBody constructor) {
    constructors.add(constructor);
  }

  public void addField(IRFieldBody field) {
    fields.add(field);
  }

  public void addSyntax(IRSyntax syntax) {
    this.syntax.add(syntax);
  }

  public void addDefaultArgument(IRDefaultArgument darg) {
    this.darguments.add(darg);
  }

  public void addStaticInitializer(IRStaticInitializer sinit) {
    this.sinits.add(sinit);
  }

  public IRHeader getIRHeader(CtClass ctcl) {
    return classenv.get(ctcl);
  }

  public ClassPool getClassPool() {
    return cpool;
  }

  public OperatorPool getOperatorPool() {
    return opool;
  }

  public Collection<CtClass> getClasses() {
    return classenv.keySet();
  }

  public Collection<IRMethodBody> getMethods() {
    return methods;
  }

  public Collection<IRConstructorBody> getConstructors() {
    return constructors;
  }

  public Collection<IRFieldBody> getFields() {
    return fields;
  }

  public Collection<IRSyntax> getSyntax() {
    return syntax;
  }

  public Collection<IRDefaultArgument> getDefaultArguments() {
    return darguments;
  }

  public Collection<IRStaticInitializer> getStaticInitializers() {
    return sinits;
  }

  private Map<CtClass, IRHeader> classenv;
  private Collection<IRMethodBody> methods;
  private Collection<IRConstructorBody> constructors;
  private Collection<IRFieldBody> fields;
  private Collection<IRSyntax> syntax;
  private Collection<IRDefaultArgument> darguments;
  private Collection<IRStaticInitializer> sinits;

  private ClassPool cpool;
  private OperatorPool opool;
}
