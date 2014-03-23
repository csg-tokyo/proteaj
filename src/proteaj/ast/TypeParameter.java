package proteaj.ast;

public class TypeParameter extends AST {
  public TypeParameter(String name, int line) {
    super(line);
    this.name = name;
  }

  public final String name;
}
