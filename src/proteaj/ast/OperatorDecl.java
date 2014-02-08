package proteaj.ast;

import java.util.List;

public class OperatorDecl extends AST {
  public OperatorDecl(String type, OperatorPattern pattern, List<Parameter> params, int priority, List<String> exceptions, int line) {
    super(line);
    this.name = null;
    this.modifiers = 0;
    this.type = type;
    this.pattern = pattern;
    this.params = params;
    this.priority = priority;
    this.exceptions = exceptions;
    this.body = null;
    this.bodyLine = 0;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void setBody(String body, int line) {
    this.body = body;
    this.bodyLine = line;
  }

  public boolean hasName() {
    return name != null;
  }

  public boolean hasThrowsException() {
    return ! exceptions.isEmpty();
  }

  public boolean hasBody() {
    return body != null;
  }

  public String getName() {
    return name;
  }

  public int getModifiers() {
    return modifiers;
  }

  public String getType() {
    return type;
  }

  public OperatorPattern getPattern() {
    return pattern;
  }

  public List<Parameter> getParams() {
    return params;
  }

  public int getPriority() {
    return priority;
  }

  public List<String> getThrowsExceptions() {
    return exceptions;
  }

  public String getBody() {
    return body;
  }

  public int getBodyLine() {
    return bodyLine;
  }

  private String name;
  private int modifiers;
  private String type;
  private OperatorPattern pattern;
  private List<Parameter> params;
  private int priority;
  private List<String> exceptions;
  private String body;
  private int bodyLine;
}

