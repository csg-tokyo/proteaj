package proteaj.error;

public abstract class CompileError extends Exception {
  public CompileError(String message, String file, int line) {
    super(message);
    this.file = file;
    this.line = line;
  }

  public CompileError at (int line) {
    this.line = line;
    return this;
  }

  public String getFile() {
    return file;
  }

  public int getLine() {
    return line;
  }

  public abstract String getKind();

  private String file;
  private int line;

  private static final long serialVersionUID = 1L;
}

