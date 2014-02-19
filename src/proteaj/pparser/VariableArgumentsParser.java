package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class VariableArgumentsParser extends PackratParser {
  /* Arguments
   *  : '(' [ Expression { ',' Expression } ] ')'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    int nargs = argTypes.size();

    if(nargs == 0) {
      assert false;
      throw new RuntimeException("invalid method or constructor. it is specified \"VARARGS\", but it has no params.");
    }

    if(! argTypes.get(nargs - 1).isArray()) {
      assert false;
      throw new RuntimeException("invalid method or constructor. it is specified \"VARARGS\", but last param type is not array");
    }

    CtClass componentType;
    try {
      componentType = argTypes.get(nargs - 1).getComponentType();
    } catch (NotFoundException e) {
      FailLog flog = new FailLog("not found component type of " + argTypes.get(nargs - 1).getName(), reader.getPos(), reader.getLine());
      return new BadAST(flog);
    }

    List<Expression> args = new ArrayList<Expression>(nargs);

    // '('
    TypedAST lbrace = KeywordParser.getParser("(").applyRule(reader, env);
    if(lbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(lbrace.getFailLog());
    }

    // "()"
    if(nargs == 1) {
      int bpos = reader.getPos();
      TypedAST rbrace = KeywordParser.getParser(")").applyRule(reader, env);
      if(! rbrace.isFail()) {
        return new VariableArguments(args, nargs, componentType);
      }
      else reader.setPos(bpos);

      TypedAST arg = ExpressionParser.getParser(componentType).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(pos);
        return new BadAST(arg.getFailLog());
      }

      args.add((Expression)arg);
    }
    else {
      TypedAST arg = ExpressionParser.getParser(argTypes.get(0)).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(pos);
        return new BadAST(arg.getFailLog());
      }

      args.add((Expression)arg);
    }

    for(int i = 1; i < nargs - 1; i++) {
      TypedAST delim = KeywordParser.getParser(",").applyRule(reader, env);
      if(delim.isFail()) {
        reader.setPos(pos);
        return new BadAST(delim.getFailLog());
      }

      TypedAST arg = ExpressionParser.getParser(argTypes.get(i)).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(pos);
        return new BadAST(arg.getFailLog());
      }

      args.add((Expression)arg);
    }

    while(true) {
      int dpos = reader.getPos();
      TypedAST delim = KeywordParser.getParser(",").applyRule(reader, env);
      if(delim.isFail()) {
        reader.setPos(dpos);
        break;
      }

      TypedAST arg = ExpressionParser.getParser(componentType).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(pos);
        return new BadAST(arg.getFailLog());
      }

      args.add((Expression)arg);
    }

    TypedAST rbrace = KeywordParser.getParser(")").applyRule(reader, env);
    if(rbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(rbrace.getFailLog());
    }

    return new VariableArguments(args, nargs, componentType);
  }

  public static VariableArgumentsParser getParser(CtClass[] argTypes) {
    List<CtClass> lst = Arrays.asList(argTypes);

    if(parsers.containsKey(lst)) return parsers.get(lst);

    VariableArgumentsParser parser = new VariableArgumentsParser(lst);
    parsers.put(lst, parser);
    return parser;
  }

  private VariableArgumentsParser(List<CtClass> argTypes) {
    this.argTypes = argTypes;
  }

  private List<CtClass> argTypes;

  private static Map<List<CtClass>, VariableArgumentsParser> parsers = new HashMap<List<CtClass>, VariableArgumentsParser>();
}

