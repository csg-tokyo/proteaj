package proteaj.ir;

import java.util.*;
import javassist.*;

public class IR {
  public IR() {
    classenv = new HashMap<CtClass, IRHeader>();
    methods = new ArrayList<IRMethod>();
    constructors = new ArrayList<IRConstructor>();
    fields = new ArrayList<IRField>();
    syntax = new ArrayList<IRSyntax>();
    darguments = new ArrayList<IRDefaultArgument>();
    sinits = new ArrayList<IRStaticInitializer>();

    this.cpool = ClassPool.getDefault();
    this.opool = new OperatorPool();
  }

  @Deprecated
  public CtClass makeClass(String name, int mods) {
    CtClass ctcl = cpool.makeClass(name);
    ctcl.setModifiers(mods);

    return ctcl;
  }

  public void addClass(CtClass scl, IRHeader hdata) {
    classenv.put(scl, hdata);
  }

  public void addMethod(IRMethod method) {
    methods.add(method);
  }

  public void addConstructor(IRConstructor constructor) {
    constructors.add(constructor);
  }

  public void addField(IRField field) {
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

  @Deprecated
  public ClassPool getClassPool() {
    return cpool;
  }

  public OperatorPool getOperatorPool() {
    return opool;
  }

  public Collection<CtClass> getClasses() {
    return classenv.keySet();
  }

  public Collection<IRMethod> getMethods() {
    return methods;
  }

  public Collection<IRConstructor> getConstructors() {
    return constructors;
  }

  public Collection<IRField> getFields() {
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
  private Collection<IRMethod> methods;
  private Collection<IRConstructor> constructors;
  private Collection<IRField> fields;
  private Collection<IRSyntax> syntax;
  private Collection<IRDefaultArgument> darguments;
  private Collection<IRStaticInitializer> sinits;

  @Deprecated
  private ClassPool cpool;
  private OperatorPool opool;
}
