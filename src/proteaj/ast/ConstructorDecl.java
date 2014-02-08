package proteaj.ast;

import java.util.List;

public class ConstructorDecl extends AST {
  public ConstructorDecl(String name, List<Parameter> params, List<String> exceptions, int line) {
    super(line);
    this.modifiers = 0;
    this.name = name;
    this.params = params;
    this.exceptions = exceptions;
    this.body = null;
    this.bodyLine = 0;
  }

  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  public void setBody(String body, int line) {
    this.body = body;
    this.bodyLine = line;
  }

  public boolean hasThrowsException() {
    return ! exceptions.isEmpty();
  }

  public boolean hasBody() {
    return body != null;
  }

  public int getModifiers() {
    return modifiers;
  }

  public String getName() {
    return name;
  }

  public List<Parameter> getParams() {
    return params;
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

  private int modifiers;
  private String name;
  private List<Parameter> params;
  private List<String> exceptions;
  private String body;
  private int bodyLine;
}

