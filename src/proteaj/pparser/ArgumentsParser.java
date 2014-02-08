package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

public class ArgumentsParser extends PackratParser {
  /* Arguments
   *  : '(' [ Expression { ',' Expression } ] ')'
   */
  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();

    TypedAST lbrace = KeywordParser.getParser("(").applyRule(reader, env);
    if(lbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(lbrace.getFailLog());
    }

    int nargs = argTypes.size();
    List<Expression> args = new ArrayList<Expression>(nargs);

    if(nargs != 0) {
      TypedAST arg = ExpressionParser.getParser(argTypes.get(0)).applyRule(reader, env);
      if(arg.isFail()) {
        reader.setPos(pos);
        return new BadAST(arg.getFailLog());
      }

      args.add((Expression)arg);

      for(int i = 1; i < nargs; i++) {
        TypedAST delim = KeywordParser.getParser(",").applyRule(reader, env);
        if(delim.isFail()) {
          reader.setPos(pos);
          return new BadAST(delim.getFailLog());
        }

        arg = ExpressionParser.getParser(argTypes.get(i)).applyRule(reader, env);
        if(arg.isFail()) {
          reader.setPos(pos);
          return new BadAST(arg.getFailLog());
        }

        args.add((Expression)arg);
      }
    }

    TypedAST rbrace = KeywordParser.getParser(")").applyRule(reader, env);
    if(rbrace.isFail()) {
      reader.setPos(pos);
      return new BadAST(rbrace.getFailLog());
    }

    return new Arguments(args);
  }

  public static ArgumentsParser getParser(CtClass[] argTypes) {
    List<CtClass> lst = Arrays.asList(argTypes);

    if(parsers.containsKey(lst)) return parsers.get(lst);

    ArgumentsParser parser = new ArgumentsParser(lst);
    parser.init();
    parsers.put(lst, parser);
    return parser;
  }

  public static void initAll() {
    for(ArgumentsParser parser : parsers.values()) {
      parser.init();
    }
  }

  private ArgumentsParser(List<CtClass> argTypes) {
    this.argTypes = argTypes;
  }

  private List<CtClass> argTypes;

  private static Map<List<CtClass>, ArgumentsParser> parsers = new HashMap<List<CtClass>, ArgumentsParser>();
}
