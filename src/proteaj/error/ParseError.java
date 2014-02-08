package proteaj.error;

public class ParseError extends CompileError {
  public ParseError(String message, String file, int line) {
    super(message, file, line);
  }

  @Override
  public String getKind() {
    return "Parse Error";
  }

  private static final long serialVersionUID = 1L;
}

