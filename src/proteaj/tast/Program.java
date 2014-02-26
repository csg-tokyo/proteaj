package proteaj.tast;

import proteaj.ir.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class Program {
  public Program(IR ir) {
    this.classes = new HashMap<CtClass, ClassDeclaration>();
    this.operatorModules = new ArrayList<OperatorModuleDeclaration>();

    for (CtClass clazz: ir.getClasses()) classes.put(clazz, new ClassDeclaration(clazz, ir.getIRHeader(clazz).getFilePath()));
    for (IRSyntax syntax: ir.getSyntax()) operatorModules.add(new OperatorModuleDeclaration(syntax));
  }

  public Collection<ClassDeclaration> getClasses() {
    return classes.values();
  }

  public Collection<OperatorModuleDeclaration> getOperatorsModules() {
    return operatorModules;
  }

  public void addMethod (CtMethod method, MethodBody body) {
    getClassDeclaration(method).addMethod(method, body);
  }

  public void addConstructor (CtConstructor constructor, ConstructorBody body) {
    getClassDeclaration(constructor).addConstructor(constructor, body);
  }

  public void addDefaultValue (CtMethod method, DefaultValue defaultValue) {
    getClassDeclaration(method).addDefaultValue(method, defaultValue);
  }

  public void addClassInitializer (CtConstructor clIni, ClassInitializer body) {
    getClassDeclaration(clIni).addClassInitializer(clIni, body);
  }

  public void addField (CtField field, FieldBody body) {
    getClassDeclaration(field).addField(field, body);
  }

  public void addMethods (List<Pair<CtMethod, MethodBody>> methods) {
    for (Pair<CtMethod, MethodBody> pair: methods) addMethod(pair.getFirst(), pair.getSecond());
  }
  public void addConstructors (List<Pair<CtConstructor, ConstructorBody>> constructors) {
    for (Pair<CtConstructor, ConstructorBody> pair: constructors) addConstructor(pair.getFirst(), pair.getSecond());
  }

  public void addFields (List<Pair<CtField, FieldBody>> fields) {
    for (Pair<CtField, FieldBody> pair: fields) addField(pair.getFirst(), pair.getSecond());
  }

  public void addDefaultValues (List<Pair<CtMethod, DefaultValue>> defaultValues) {
    for (Pair<CtMethod, DefaultValue> pair: defaultValues) addDefaultValue(pair.getFirst(), pair.getSecond());
  }

  public void addClassInitializers (List<Pair<CtConstructor, ClassInitializer>> classInitializers) {
    for (Pair<CtConstructor, ClassInitializer> pair: classInitializers) addClassInitializer(pair.getFirst(), pair.getSecond());
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
