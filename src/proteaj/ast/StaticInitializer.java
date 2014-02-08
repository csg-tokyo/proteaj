package proteaj.ast;

public class StaticInitializer extends AST {
  public StaticInitializer(String body, int bline, int line) {
    super(line);
    this.body = body;
    this.bodyLine = bline;
  }

  public String getBody() {
    return body;
  }

  public int getBodyLine() {
    return bodyLine;
  }

  private String body;
  private int bodyLine;
}

