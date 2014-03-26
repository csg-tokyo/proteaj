package proteaj.tast;

import proteaj.ir.*;

import java.util.*;
import javassist.*;

public class Program {
  public Program(IR ir) {
    this.classes = new HashMap<>();
    this.operatorModules = new ArrayList<>();

    for (CtClass clazz: ir.getClasses()) classes.put(clazz, new ClassDeclaration(clazz, ir.getIRHeader(clazz).filePath));
    for (IRSyntax syntax: ir.getSyntax()) operatorModules.add(new OperatorModuleDeclaration(syntax));
  }

  public Program(List<ClassDeclaration> classes, List<OperatorModuleDeclaration> operatorModules) {
    this.classes = new HashMap<>();
    this.operatorModules = operatorModules;

    for(ClassDeclaration c : classes) this.classes.put(c.clazz, c);
  }

  public ClassDeclaration getClass(CtClass clazz) {
    return classes.get(clazz);
  }

  public Collection<ClassDeclaration> getClasses() {
    return classes.values();
  }

  public List<OperatorModuleDeclaration> getOperatorsModules() {
    return operatorModules;
  }

  public void addClass (ClassDeclaration clazz) {
    classes.put(clazz.clazz, clazz);
  }

  public void addMethod (MethodDeclaration method) {
    getClassDeclaration(method.method).addMethod(method);
  }

  public void addConstructor (ConstructorDeclaration constructor) {
    getClassDeclaration(constructor.constructor).addConstructor(constructor);
  }

  public void addDefaultValue (DefaultValueDefinition defaultValue) {
    getClassDeclaration(defaultValue.method).addDefaultValue(defaultValue);
  }

  public void addClassInitializer (ClassInitializerDefinition clIni) {
    getClassDeclaration(clIni.clIni).addClassInitializer(clIni);
  }

  public void addField (FieldDeclaration field) {
    getClassDeclaration(field.field).addField(field);
  }

  public void addClasses (List<ClassDeclaration> classes) {
    for (ClassDeclaration clazz : classes) addClass(clazz);
  }

  public void addMethods (List<MethodDeclaration> methods) {
    for (MethodDeclaration method : methods) addMethod(method);
  }
  public void addConstructors (List<ConstructorDeclaration> constructors) {
    for (ConstructorDeclaration constructor : constructors) addConstructor(constructor);
  }

  public void addFields (List<FieldDeclaration> fields) {
    for (FieldDeclaration field : fields) addField(field);
  }

  public void addDefaultValues (List<DefaultValueDefinition> defaultValues) {
    for (DefaultValueDefinition d : defaultValues) addDefaultValue(d);
  }

  public void addClassInitializers (List<ClassInitializerDefinition> classInitializers) {
    for (ClassInitializerDefinition clIni : classInitializers) addClassInitializer(clIni);
  }

  private ClassDeclaration getClassDeclaration (CtMember member) {
    CtClass clazz = member.getDeclaringClass();
    if (! classes.containsKey(clazz)) {
      assert false;
      throw new RuntimeException("add a member to an unknown/existing class");
    }
    return classes.get(clazz);
  }

  private Map<CtClass, ClassDeclaration> classes;
  private List<OperatorModuleDeclaration> operatorModules;
}
