package proteaj.util;

public class Brackets {
  public static int getLeft (int right) {
    switch (right) {
      case ')': return '(';
      case ']': return '[';
      case '}': return '{';
      default : return -1;
    }
  }
}
