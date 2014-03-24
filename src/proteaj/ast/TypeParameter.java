package proteaj.ast;

public class TypeParameter extends AST {
  public TypeParameter(String name, int line) {
    super(line);
    this.name = name;
    this.bound = "java.lang.Object";
  }

  public TypeParameter(String name, String bound, int line) {
    super(line);
    this.name = name;
    this.bound = bound;
  }

  public final String name;
  public final String bound;
}
