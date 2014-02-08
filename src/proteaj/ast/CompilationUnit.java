package proteaj.ast;

public class CompilationUnit extends AST {
  public CompilationUnit(String filePath, FileHeader header, FileBody body) {
    super(header);
    this.filePath = filePath;
    this.header = header;
    this.body = body;
  }

  public String getFilePath() {
    return filePath;
  }

  public FileHeader getHeader() {
    return header;
  }

  public FileBody getBody() {
    return body;
  }

  private String filePath;
  private FileHeader header;
  private FileBody body;
}

