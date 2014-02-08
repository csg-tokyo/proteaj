package proteaj.token;

import java.util.*;
import static proteaj.util.Escape.*;

public class CharLiteral extends Token {
  public CharLiteral(char c, int line) {
    super(line);
    this.c = c;
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @Override
  public String toString() {
    return "\'" + escape(c) + "\'";
  }

  private char c;
}

