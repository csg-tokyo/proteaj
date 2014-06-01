package proteaj.error;

public class Warning {
  public static void print(String msg) {
    System.err.println("Warning: " + msg);
  }

  public static void print(String msg, String file, int line) {
    System.err.println("** Warning ** " + msg + "\n  line " + line + " [ " + file + " ]");
  }
}

