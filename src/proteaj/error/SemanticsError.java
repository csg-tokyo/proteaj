package proteaj.error;

import javassist.CannotCompileException;

public class SemanticsError extends CompileError {
  public SemanticsError(String str, String file, int line) {
    super(str, file, line);
  }

  public SemanticsError (CannotCompileException e, String file, int line) {
    super(e.getMessage(), file, line);
  }

  public SemanticsError (CannotCompileException e, String file) {
    super(e.getMessage(), file, 0);
  }

  @Override
  public String getKind() {
    return "Semantic Error";
  }

  private static final long serialVersionUID = 1L;
}
