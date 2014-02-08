package proteaj.pparser;

import proteaj.TypeResolver;
import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class NewArrayExpressionParser extends PackratParser {
  /* NewArrayExpression
   *  : "new" ClassName '[' Expression ']' { '[' Expression ']' } { '[' ']' }
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    // "new"
    TypedAST newkeyword = KeywordParser.getParser("new").applyRule(reader, env);
    if(newkeyword.isFail()) {
      reader.setPos(pos);
      return new BadAST(newkeyword.getFailLog());
    }

    // ClassName
    TypedAST clsName = ClassNameParser.parser.applyRule(reader, env);
    if(clsName.isFail()) {
      reader.setPos(pos);
      return new BadAST(clsName.getFailLog());
    }

    StringBuilder typeNameBuf = new StringBuilder(((ClassName)clsName).getCtClass().getName());
    List<Expression> args = new ArrayList<Expression>();

    // '['
    TypedAST lbracket = KeywordParser.getParser("[").applyRule(reader, env);
    if(lbracket.isFail()) {
      reader.setPos(pos);
      return new BadAST(lbracket.getFailLog());
    }

    // Expression
    TypedAST arg = ExpressionParser.getParser(CtClass.intType).applyRule(reader, env);
    if(arg.isFail()) {
      reader.setPos(pos);
      return new BadAST(arg.getFailLog());
    }

    args.add((Expression)arg);

    // ']'
    TypedAST rbracket = KeywordParser.getParser("]").applyRule(reader, env);
    if(rbracket.isFail()) {
      reader.setPos(pos);
      return new BadAST(rbracket.getFailLog());
    }

    typeNameBuf.append("[]");

    while(true) {
      int bpos = reader.getPos();

      // "["
      lbracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lbracket.isFail()) try {
        CtClass arrayType = resolver.getType(typeNameBuf.toString());
        return new NewArrayExpression(arrayType, args);
      } catch (NotFoundError e) {
        FailLog flog = new FailLog("unknown type : " + typeNameBuf.toString(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      // Expression
      arg = ExpressionParser.getParser(CtClass.intType).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(bpos);
        break;
      }

      args.add((Expression)arg);

      // "]"
      rbracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rbracket.isFail()) {
        reader.setPos(pos);
        return new BadAST(rbracket.getFailLog());
      }

      typeNameBuf.append("[]");
    }

    while(true) {
      // "["
      lbracket = KeywordParser.getParser("[").applyRule(reader, env);
      if(lbracket.isFail()) try {
        CtClass arrayType = resolver.getType(typeNameBuf.toString());
        return new NewArrayExpression(arrayType, args);
      } catch (NotFoundError e) {
        FailLog flog = new FailLog("unknown type : " + typeNameBuf.toString(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      // "]"
      rbracket = KeywordParser.getParser("]").applyRule(reader, env);
      if(rbracket.isFail()) {
        reader.setPos(pos);
        return new BadAST(rbracket.getFailLog());
      }

      typeNameBuf.append("[]");
    }
  }

  public void init(TypeResolver resolver) {
    this.resolver = resolver;
    super.init();
  }

  @Override
  public String toString() {
    return "NewArrayExpressionParser";
  }

  public static final NewArrayExpressionParser parser = new NewArrayExpressionParser();

  private TypeResolver resolver;

  private NewArrayExpressionParser() {}
}

