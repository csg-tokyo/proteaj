package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.isStatic;

public class FieldAccessParser extends PackratParser<Expression> {
  /* FieldAccess
   *  : JavaExpression '.' Identifier
   */
  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    // JavaExpression
    ParseResult<Expression> expr = JavaExpressionParser.parser.applyRule(reader, env);
    if(expr.isFail()) return fail(expr, pos, reader);

    // '.'
    ParseResult<String> dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) return fail(dot, pos, reader);

    // Identifier
    ParseResult<String> identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) return fail(identifier, pos, reader);

    if(expr.get().getType() == env.thisClass) {
      for(CtField field : expr.get().getType().getDeclaredFields()) {
        if((! isStatic(field.getModifiers())) && field.getName().equals(identifier.get())) try {
          return success(new FieldAccess(expr.get(), field));
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
          break;
        }
      }
    }

    for(CtField field : expr.get().getType().getFields()) {
      if((! isStatic(field.getModifiers())) && field.visibleFrom(env.thisClass) && field.getName().equals(identifier.get())) try {
        return success(new FieldAccess(expr.get(), field));
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
        break;
      }
    }

    // fail
    return fail("undefined field : " + identifier.get(), pos, reader);
  }

  @Override
  public String toString() {
    return "FieldAccessParser";
  }

  public static final FieldAccessParser parser = new FieldAccessParser();

  private FieldAccessParser() {}
}

