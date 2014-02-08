package proteaj.ast;

public class Parameter extends AST {
  public Parameter(String type, String name, int line) {
    super(line);
    this.modifiers = 0;
    this.type = type;
    this.name = name;
    this.defaultValue = null;
    this.defaultValueLine = 0;
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void setDefaultValue(String value, int line) {
    this.defaultValue = value;
    this.defaultValueLine = line;
  }

  public boolean hasDefaultValue() {
    return defaultValue != null;
  }

  public int getModifiers() {
    return modifiers;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public int getDefaultValueLine() {
    return defaultValueLine;
  }

  private int modifiers;
  private String type;
  private String name;
  private String defaultValue;
  private int defaultValueLine;
}

