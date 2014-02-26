package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

public class ArgumentsParser extends PackratParser<Arguments> {
  /* Arguments
   *  : '(' [ Expression { ',' Expression } ] ')'
   */
  @Override
  protected ParseResult<Arguments> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    ParseResult<String> lBrace = KeywordParser.getParser("(").applyRule(reader, env);
    if(lBrace.isFail()) return fail(lBrace, pos, reader);

    int nargs = argTypes.size();
    List<Expression> args = new ArrayList<Expression>(nargs);

    if(nargs != 0) {
      ParseResult<Expression> arg = ExpressionParser.getParser(argTypes.get(0), env).applyRule(reader, env);
      if(arg.isFail()) return fail(arg, pos, reader);

      args.add(arg.get());

      for(int i = 1; i < nargs; i++) {
        ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
        if(delim.isFail()) return fail(delim, pos, reader);

        arg = ExpressionParser.getParser(argTypes.get(i), env).applyRule(reader, env);
        if(arg.isFail()) return fail(arg, pos, reader);

        args.add(arg.get());
      }
    }

    ParseResult<String> rBrace = KeywordParser.getParser(")").applyRule(reader, env);
    if(rBrace.isFail()) return fail(rBrace, pos, reader);

    return success(new Arguments(args));
  }

  public static ArgumentsParser getParser(CtClass[] argTypes) {
    List<CtClass> lst = Arrays.asList(argTypes);

    if(parsers.containsKey(lst)) return parsers.get(lst);

    ArgumentsParser parser = new ArgumentsParser(lst);
    parsers.put(lst, parser);
    return parser;
  }

  private ArgumentsParser(List<CtClass> argTypes) {
    this.argTypes = argTypes;
  }

  private List<CtClass> argTypes;

  private static Map<List<CtClass>, ArgumentsParser> parsers = new HashMap<List<CtClass>, ArgumentsParser>();
}
