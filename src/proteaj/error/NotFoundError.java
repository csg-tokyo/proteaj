package proteaj.error;

import javassist.NotFoundException;

public class NotFoundError extends CompileError {
  public NotFoundError(String msg, String file, int line) {
    super(msg, file, line);
  }

  public NotFoundError(NotFoundException e, String file, int line) {
    super(e.getMessage(), file, line);
  }

  public NotFoundError(NotFoundException e, String file) {
    this(e, file, 0);
  }

  @Override
  public String getKind() {
    return "Not Found Error";
  }

  private static final long serialVersionUID = 1L;
}
