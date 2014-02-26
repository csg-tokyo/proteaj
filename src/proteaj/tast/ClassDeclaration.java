package proteaj.tast;

import java.util.*;
import javassist.*;

public class ClassDeclaration {
  public ClassDeclaration (CtClass clazz, String filePath) {
    this.clazz = clazz;
    this.filePath = filePath;

    this.methods = new HashMap<CtMethod, MethodBody>();
    this.constructors = new HashMap<CtConstructor, ConstructorBody>();
    this.fields = new HashMap<CtField, FieldBody>();
    this.defaultValues = new HashMap<CtMethod, DefaultValue>();
    this.initializers = new HashMap<CtConstructor, List<ClassInitializer>>();
  }

  public void addMethod (CtMethod method, MethodBody body) {
    methods.put(method, body);
  }

  public void addConstructor (CtConstructor constructor, ConstructorBody body) {
    constructors.put(constructor, body);
  }

  public void addField (CtField field, FieldBody body) {
    fields.put(field, body);
  }

  public void addDefaultValue (CtMethod method, DefaultValue defaultValue) {
    defaultValues.put(method, defaultValue);
  }

  public void addClassInitializer (CtConstructor clIni, ClassInitializer body) {
    if (! initializers.containsKey(clIni)) initializers.put(clIni, new ArrayList<ClassInitializer>());
    initializers.get(clIni).add(body);
  }

  public Map<CtMethod, MethodBody> getMethods() { return methods; }
  public Map<CtConstructor, ConstructorBody> getConstructors() { return constructors; }
  public Map<CtField, FieldBody> getFields() { return fields; }
  public Map<CtMethod, DefaultValue> getDefaultValues() { return defaultValues; }
  public Map<CtConstructor, List<ClassInitializer>> getInitializers() { return initializers; }

  public final CtClass clazz;
  public final String filePath;

  private Map<CtMethod, MethodBody> methods;
  private Map<CtConstructor, ConstructorBody> constructors;
  private Map<CtField, FieldBody> fields;
  private Map<CtMethod, DefaultValue> defaultValues;
  private Map<CtConstructor, List<ClassInitializer>> initializers;
}
