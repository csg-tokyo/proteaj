package proteaj.tast;

import java.util.*;
import javassist.*;
import proteaj.ir.IRClass;

public class ClassDeclaration {
  public ClassDeclaration (IRClass clazz, String filePath) {
    this(clazz.clazz, filePath, clazz.getDeclaredFields_Ordered());
  }

  public ClassDeclaration (CtClass clazz, String filePath, List<CtField> fields_ordered) {
    this.clazz = clazz;
    this.filePath = filePath;

    this.methods = new HashMap<>();
    this.fields = new HashMap<>();
    this.constructors = new ArrayList<>();
    this.defaultValues = new ArrayList<>();
    this.initializers = new ArrayList<>();

    this.fields_ordered = fields_ordered;
  }

  public void addMethod (MethodDeclaration method) {
    methods.put(method.method, method);
  }

  public void addConstructor (ConstructorDeclaration constructor) {
    constructors.add(constructor);
  }

  public void addField (FieldDeclaration field) {
    fields.put(field.field, field);
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

  public FieldDeclaration getField(CtField field) { return fields.get(field); }

  public Collection<MethodDeclaration> getMethods() { return methods.values(); }
  public Collection<FieldDeclaration> getFields() { return fields.values(); }
  public List<ConstructorDeclaration> getConstructors() { return constructors; }
  public List<DefaultValueDefinition> getDefaultValues() { return defaultValues; }
  public List<ClassInitializerDefinition> getInitializers() { return initializers; }

  public List<CtField> getDeclaredFields_Ordered () { return fields_ordered; }

  public final CtClass clazz;
  public final String filePath;

  private Map<CtMethod, MethodDeclaration> methods;
  private Map<CtField, FieldDeclaration> fields;
  private List<ConstructorDeclaration> constructors;
  private List<DefaultValueDefinition> defaultValues;
  private List<ClassInitializerDefinition> initializers;

  private List<CtField> fields_ordered;
}
