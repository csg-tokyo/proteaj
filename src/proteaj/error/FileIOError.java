package proteaj.error;

public class FileIOError extends CompileError {
  public FileIOError(String message, String file, int line) {
    super(message, file, line);
  }

  @Override
  public String getKind() {
    return "File IO Error";
  }

  private static final long serialVersionUID = 1L;
}
