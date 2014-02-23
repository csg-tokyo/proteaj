package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class VariableArgumentsParser extends PackratParser<Arguments> {
  /* Arguments
   *  : '(' [ Expression { ',' Expression } ] ')'
   */
  @Override
  protected ParseResult<Arguments> parse(SourceStringReader reader, Environment env) {
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
      return fail("not found component type of " + argTypes.get(nargs - 1).getName(), pos, reader);
    }

    List<Expression> args = new ArrayList<Expression>(nargs);

    // '('
    ParseResult<String> lBrace = KeywordParser.getParser("(").applyRule(reader, env);
    if(lBrace.isFail()) return fail(lBrace, pos, reader);

    // "()"
    if(nargs == 1) {
      int bpos = reader.getPos();

      ParseResult<String> rBrace = KeywordParser.getParser(")").applyRule(reader, env);
      if(! rBrace.isFail()) return success(new VariableArguments(args, nargs, componentType));
      else reader.setPos(bpos);

      ParseResult<Expression> arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
      if(arg.isFail()) return fail(arg, pos, reader);

      args.add(arg.get());
    }
    else {
      ParseResult<Expression> arg = ExpressionParser.getParser(argTypes.get(0), env).applyRule(reader, env);
      if(arg.isFail()) return fail(arg, pos, reader);

      args.add(arg.get());
    }

    for(int i = 1; i < nargs - 1; i++) {
      ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
      if(delim.isFail()) return fail(delim, pos, reader);

      ParseResult<Expression> arg = ExpressionParser.getParser(argTypes.get(i), env).applyRule(reader, env);
      if(arg.isFail()) return fail(arg, pos, reader);

      args.add(arg.get());
    }

    while(true) {
      int dpos = reader.getPos();
      ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
      if(delim.isFail()) {
        reader.setPos(dpos);
        break;
      }

      ParseResult<Expression> arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
      if(arg.isFail()) return fail(arg, pos, reader);

      args.add(arg.get());
    }

    ParseResult<String> rBrace = KeywordParser.getParser(")").applyRule(reader, env);
    if(rBrace.isFail()) return fail(rBrace, pos, reader);

    return success(new VariableArguments(args, nargs, componentType));
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

