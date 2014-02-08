package proteaj.error;

import java.util.*;

public class CompileErrors extends Exception {
  private static final long serialVersionUID = 1L;

  public CompileErrors(List<CompileError> errors) {
    this.errors = errors;
  }

  public List<CompileError> getErrors() {
    return errors;
  }

  private List<CompileError> errors;
}

