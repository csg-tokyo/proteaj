package proteaj.tast;

import java.util.*;
import javassist.*;

public class ClassDeclaration {
  public ClassDeclaration (CtClass clazz, String filePath) {
    this.clazz = clazz;
    this.filePath = filePath;

    this.methods = new HashMap<CtMethod, MethodDeclaration>();
    this.constructors = new ArrayList<ConstructorDeclaration>();
    this.fields = new ArrayList<FieldDeclaration>();
    this.defaultValues = new ArrayList<DefaultValueDefinition>();
    this.initializers = new ArrayList<ClassInitializerDefinition>();
  }

  public void addMethod (MethodDeclaration method) {
    methods.put(method.method, method);
  }

  public void addConstructor (ConstructorDeclaration constructor) {
    constructors.add(constructor);
  }

  public void addField (FieldDeclaration field) {
    fields.add(field);
  }

  public void addDefaultValue (DefaultValueDefinition defaultValue) {
    defaultValues.add(defaultValue);
  }

  public void addClassInitializer (ClassInitializerDefinition clIni) {
    initializers.add(clIni);
  }

  public MethodDeclaration getMethod(CtMethod method) {
    return methods.get(method);
  }

  public Collection<MethodDeclaration> getMethods() { return methods.values(); }
  public List<ConstructorDeclaration> getConstructors() { return constructors; }
  public List<FieldDeclaration> getFields() { return fields; }
  public List<DefaultValueDefinition> getDefaultValues() { return defaultValues; }
  public List<ClassInitializerDefinition> getInitializers() { return initializers; }

  public final CtClass clazz;
  public final String filePath;

  private Map<CtMethod, MethodDeclaration> methods;
  private List<ConstructorDeclaration> constructors;
  private List<FieldDeclaration> fields;
  private List<DefaultValueDefinition> defaultValues;
  private List<ClassInitializerDefinition> initializers;
}
