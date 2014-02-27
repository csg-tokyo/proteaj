package proteaj.error;

import java.util.*;

public class CompileErrors extends Exception {
  private static final long serialVersionUID = 1L;

  public CompileErrors (List<CompileError> errors) {
    this.errors = errors;
  }

  public CompileErrors (CompileError error) {
    this.errors = Arrays.asList(error);
  }

  public List<CompileError> getErrors() {
    return errors;
  }

  private List<CompileError> errors;
}

