package proteaj.ir.primitive;

import proteaj.ir.*;
import static proteaj.ir.IRCommonTypes.*;

public class PrimitiveReadasSyntax extends IRSyntax {
  public static PrimitiveReadasSyntax getSyntax() {
    if(syntax == null) syntax = new PrimitiveReadasSyntax();
    return syntax;
  }

  private PrimitiveReadasSyntax() {
    super("proteaj.lang.PrimitiveReadasOperators");

    if(getIdentifierType() != null) addOperator(new PrimitiveReadasOperator(getIdentifierType()));
    if(getLetterType() != null) addOperator(new PrimitiveReadasOperator(getLetterType()));
    if(getTypeType() != null) addOperator(new PrimitiveReadasOperator(getTypeType()));
  }

  private static PrimitiveReadasSyntax syntax = null;
}

