package proteaj.ast;

public class CompilationUnit extends AST {
  public CompilationUnit(String filePath, FileHeader header, FileBody body) {
    super(header);
    this.filePath = filePath;
    this.header = header;
    this.body = body;
  }

  public final String filePath;
  public final FileHeader header;
  public final FileBody body;
}

