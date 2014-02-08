package proteaj.error;

public class LexicalError extends CompileError {
  public LexicalError(String message, String file, int line) {
    super(message, file, line);
  }

  @Override
  public String getKind() {
    return "Lexical Error";
  }

  private static final long serialVersionUID = 1L;
}

