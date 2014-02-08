package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import javassist.*;

import static proteaj.util.Modifiers.isStatic;

public class FieldAccessParser extends PackratParser {
  /* FieldAccess
   *  : JavaExpression '.' Identifier
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // JavaExpression
    TypedAST jexpr = JavaExpressionParser.parser.applyRule(reader, env);
    if(jexpr.isFail()) {
      reader.setPos(pos);
      return new BadAST(jexpr.getFailLog());
    }

    Expression expr = (Expression)jexpr;

    // '.'
    TypedAST dot = KeywordParser.getParser(".").applyRule(reader, env);
    if(dot.isFail()) {
      reader.setPos(pos);
      return new BadAST(dot.getFailLog());
    }

    // Identifier
    TypedAST identifier = IdentifierParser.parser.applyRule(reader, env);
    if(identifier.isFail()) {
      reader.setPos(pos);
      return new BadAST(identifier.getFailLog());
    }

    String name = ((Identifier)identifier).getName();

    if(expr.getType() == thisClass) {
      for(CtField field : expr.getType().getDeclaredFields()) {
        if((! isStatic(field.getModifiers())) && field.getName().equals(name)) try {
          return new FieldAccess(expr, field);
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
          break;
        }
      }
    }

    for(CtField field : expr.getType().getFields()) {
      if((! isStatic(field.getModifiers())) && field.visibleFrom(thisClass) && field.getName().equals(name)) try {
        return new FieldAccess(expr, field);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.getFilePath(), reader.getLine()));
        break;
      }
    }

    // fail
    FailLog flog = new FailLog("undefined field : " + name, reader.getPos(), reader.getLine());
    reader.setPos(pos);
    return new BadAST(flog);
  }

  public void init(CtClass thisClass) {
    this.thisClass = thisClass;
    super.init();
  }

  @Override
  public String toString() {
    return "FieldAccessParser";
  }

  public static final FieldAccessParser parser = new FieldAccessParser();

  private CtClass thisClass;

  private FieldAccessParser() {}
}

