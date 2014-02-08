package proteaj.error;

public class FailLog {
  public FailLog(String msg, int epos, int line) {
    this.msg = msg;
    this.epos = epos;
    this.line = line;
  }

  public String getMessage() {
    return msg;
  }

  public int getEndPosition() {
    return epos;
  }

  public int getLine() {
    return line;
  }

  private String msg;
  private int epos;
  private int line;
}

