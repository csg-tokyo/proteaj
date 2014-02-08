package proteaj.util;

public class Escape {
  public static String escape(String str) {
    return str.replace("\\", "\\\\")
        .replace("\'", "\\\'")
        .replace("\"", "\\\"")
        .replace("\b", "\\b")
        .replace("\t", "\\t")
        .replace("\n", "\\n")
        .replace("\f", "\\f")
        .replace("\r", "\\r");
  }

  public static String escape (char c) {
    switch (c) {
      case '\\' : return "\\\\";
      case '\'' : return "\\'";
      case '\"' : return "\\\"";
      case '\b' : return "\\b";
      case '\t' : return "\\t";
      case '\n' : return "\\n";
      case '\f' : return "\\f";
      case '\r' : return "\\r";
      default   : return Character.toString(c);
    }
  }
}
