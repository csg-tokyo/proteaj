package proteaj.error;

import java.util.*;

public class ErrorList {
  public static void init() {
    errors = new HashMap<String, List<CompileError>>();
  }

  public static void addErrors (CompileErrors es) {
    for (CompileError e : es.getErrors()) addError(e);
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
    int num = 0;
    List<Map.Entry<String, List<CompileError>>> es = new ArrayList<>(errors.entrySet());
    Collections.sort(es, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));

    for(Map.Entry<String, List<CompileError>> entry : es) {
      assert ! entry.getValue().isEmpty();

      Collections.sort(entry.getValue(), CompileErrorComparator.instance);

      System.err.println('[' + entry.getKey() + ']');

      for(CompileError e : entry.getValue()) {
        num++;
        if(e.getLine() != 0) System.err.println("line " + e.getLine() + " : << " + e.getKind() + " >> " + e.getMessage());
        else System.err.println("<< " + e.getKind() + " >> " + e.getMessage());
      }
    }
    System.err.println(num + " errors");
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

