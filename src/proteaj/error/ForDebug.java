package proteaj.error;

import java.util.HashSet;
import java.util.Set;

public class ForDebug {
  public static void print(String msg) {
    if (verbose) {
      if (! msgs.contains(msg)) {
        msgs.add(msg);
        System.err.println(msg);
      }
    }
  }

  public static void setVerboseFlag() {
    verbose = true;
    msgs = new HashSet<>();
  }

  private static boolean verbose = false;
  private static Set<String> msgs;
}
