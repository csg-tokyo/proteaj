package proteaj.error;

import java.util.*;

public class ErrorList {
  public static void init() {
    errors = new HashMap<String, List<CompileError>>();
  }

  public static void addError(CompileError e) {
    String file = e.getFile();

    if(errors.containsKey(file)) {
      errors.get(file).add(e);
    }
    else {
      List<CompileError> es = new ArrayList<CompileError>();
      es.add(e);
      errors.put(file, es);
    }
  }

  public static boolean hasError() {
    return ! errors.isEmpty();
  }

  public static void printAllErrors() {
    for(Map.Entry<String, List<CompileError>> entry : errors.entrySet()) {
      assert ! entry.getValue().isEmpty();

      Collections.sort(entry.getValue(), CompileErrorComparator.instance);

      System.err.println('[' + entry.getKey() + ']');

      for(CompileError e : entry.getValue()) {
        if(e.getLine() != 0) System.err.println("line " + e.getLine() + " : << " + e.getKind() + " >> " + e.getMessage());
        else System.err.println("<< " + e.getKind() + " >> " + e.getMessage());
      }
    }
  }

  private static Map<String, List<CompileError>> errors;
}

enum CompileErrorComparator implements Comparator<CompileError> {
  instance {
    @Override
    public int compare(CompileError e0, CompileError e1) {
      return e0.getLine() - e1.getLine();
    }
  }
}

