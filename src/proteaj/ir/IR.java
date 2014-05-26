package proteaj.ir;

import java.util.*;
import javassist.*;

public class IR {
  public IR() {
    classes = new ArrayList<>();

    methods = new ArrayList<>();
    constructors = new ArrayList<>();
    fields = new ArrayList<>();
    syntax = new ArrayList<>();
    darguments = new ArrayList<>();
    sinits = new ArrayList<>();

    this.opool = new OperatorPool();
  }

  public void addClass (IRClass clazz) {
    classIRHeaderMap.put(clazz.clazz, clazz.header);
    classes.add(clazz);
  }

  public List<IRClass> getClasses () { return classes; }

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
    opool.addSyntax(syntax);
    this.syntax.add(syntax);
  }

  public void addDefaultArgument(IRDefaultArgument darg) {
    this.darguments.add(darg);
  }

  public void addStaticInitializer(IRStaticInitializer sinit) {
    this.sinits.add(sinit);
  }

  public OperatorPool getOperatorPool() {
    return opool;
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

  private List<IRClass> classes;

  private Collection<IRMethod> methods;
  private Collection<IRConstructor> constructors;
  private Collection<IRField> fields;
  private Collection<IRSyntax> syntax;
  private Collection<IRDefaultArgument> darguments;
  private Collection<IRStaticInitializer> sinits;

  private OperatorPool opool;

  @Deprecated
  public IRHeader getIRHeader(CtClass ctClass) {
    return classIRHeaderMap.get(ctClass);
  }

  @Deprecated
  private Map<CtClass, IRHeader> classIRHeaderMap = new HashMap<>();
}
