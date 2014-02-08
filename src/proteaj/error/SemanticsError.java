package proteaj.error;

public class SemanticsError extends CompileError {
  public SemanticsError(String str, String file, int line) {
    super(str, file, line);
  }

  @Override
  public String getKind() {
    return "Semantic Error";
  }

  private static final long serialVersionUID = 1L;
}
