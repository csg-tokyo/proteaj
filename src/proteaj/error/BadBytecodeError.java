package proteaj.error;

import javassist.bytecode.BadBytecode;

public class BadBytecodeError extends CompileError {
  public BadBytecodeError(BadBytecode e, String file, int line) {
    super(e.getMessage(), file, line);
  }

  @Override
  public String getKind() {
    return "Bad Bytecode Error";
  }

  private static final long serialVersionUID = 1L;
}
