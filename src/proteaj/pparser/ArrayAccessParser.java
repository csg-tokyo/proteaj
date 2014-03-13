package proteaj.pparser;

import proteaj.ir.*;
import proteaj.tast.*;

import javassist.*;

import static proteaj.pparser.PackratParserCombinators.*;

public class ArrayAccessParser {
  /* ArrayAccess
   *  : JavaExpression '[' Expression ']'
   */
  public static final PackratParser<ArrayAccess> parser =
      bind(ref(new ParserThunk<Expression>() {
        @Override
        public PackratParser<Expression> getParser() { return JavaExpressionParser.parser; }
      }), new Function<Expression, PackratParser<ArrayAccess>>() {
        @Override
        public PackratParser<ArrayAccess> apply(final Expression array) {
          return bind(enclosed("[", depends(new Function<Environment, PackratParser<Expression>>() {
            @Override
            public PackratParser<Expression> apply(Environment env) { return ExpressionParser.getParser(CtClass.intType, env); }
          }), "]"), new Function<Expression, PackratParser<ArrayAccess>>() {
            @Override
            public PackratParser<ArrayAccess> apply(Expression index) {
              if (array.getType().isArray()) try { return unit(new ArrayAccess(array, index)); }
              catch (NotFoundException e) { return failure(e.getMessage()); }
              else return failure("not array type");
            }
          });
        }
      });
}
