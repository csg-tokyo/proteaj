package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.util.Modifiers.hasVarArgs;

public class ArgumentsParser extends PackratParser<List<Expression>> {
  /* Arguments
   *  : '(' [ Expression { ',' Expression } ] ')'
   */
  @Override
  protected ParseResult<List<Expression>> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();

    final CtClass[] argTypes;
    try {
      argTypes = behavior.getParameterTypes();
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      return fail("unknown argument types", pos, reader);
    }

    // '('
    ParseResult<String> lBrace = KeywordParser.getParser("(").applyRule(reader, env);
    if (lBrace.isFail()) return fail(lBrace, pos, reader);

    final ParseResult<List<Expression>> args;
    if (hasVarArgs(behavior.getModifiers())) args = parseVarArguments(pos, argTypes, reader, env);
    else args = parseNormalArguments(pos, argTypes, reader, env);

    // ')'
    ParseResult<String> rBrace = KeywordParser.getParser(")").applyRule(reader, env);
    if (rBrace.isFail()) return fail(rBrace, pos, reader);

    return args;
  }

  private ParseResult<List<Expression>> parseNormalArguments(int pos, CtClass[] argTypes, SourceStringReader reader, Environment env) {
    List<Expression> args = new ArrayList<Expression>();

    if (argTypes.length == 0) return success(args);

    ParseResult<Expression> arg = ExpressionParser.getParser(argTypes[0], env).applyRule(reader, env);
    if (arg.isFail()) return fail(arg, pos, reader);
    else args.add(arg.get());

    for (int i = 1; i < argTypes.length; i++) {
      ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
      if (delim.isFail()) return fail(delim, pos, reader);

      arg = ExpressionParser.getParser(argTypes[i], env).applyRule(reader, env);
      if (arg.isFail()) return fail(arg, pos, reader);
      else args.add(arg.get());
    }

    return success(args);
  }

  private ParseResult<List<Expression>> parseVarArguments(int pos, CtClass[] argTypes, SourceStringReader reader, Environment env) {
    if (argTypes.length == 0) return success(new ArrayList<Expression>());
    if (argTypes.length == 1) return parseVarArguments1(pos, argTypes[0], reader, env);

    List<Expression> args = new ArrayList<Expression>();

    for (int i = 0; i < argTypes.length - 2; i++) {
      ParseResult<Expression> arg = ExpressionParser.getParser(argTypes[i], env).applyRule(reader, env);
      if (arg.isFail()) return fail(arg, pos, reader);
      else args.add(arg.get());

      ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
      if (delim.isFail()) return fail(delim, pos, reader);
    }

    ParseResult<Expression> arg = ExpressionParser.getParser(argTypes[argTypes.length - 2], env).applyRule(reader, env);
    if (arg.isFail()) return fail(arg, pos, reader);
    else args.add(arg.get());

    int dPos = reader.getPos();
    CtClass arrayType = argTypes[argTypes.length - 1];

    // [ ',' ... ]
    ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
    if (delim.isFail()) {
      reader.setPos(dPos);
      args.add(new VariableArguments(arrayType));
      return success(args);
    }

    int aPos = reader.getPos();

    // Expression<ArrayType>
    arg = ExpressionParser.getParser(arrayType, env).applyRule(reader, env);
    if (! arg.isFail()) {
      args.add(arg.get());
      return success(args);
    }

    reader.setPos(aPos);
    List<Expression> varArgs = new ArrayList<Expression>();

    final CtClass componentType;
    try {
      componentType = arrayType.getComponentType();
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      return fail("not found component type of " + arrayType.getName(), pos, reader);
    }

    // Expression<ComponentType>
    arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
    if (arg.isFail()) return fail(arg, pos, reader);
    else varArgs.add(arg.get());

    // { ',' Expression<ComponentType> }
    while (true) {
      dPos = reader.getPos();
      delim = KeywordParser.getParser(",").applyRule(reader, env);
      if (delim.isFail()) {
        reader.setPos(dPos);
        args.add(new VariableArguments(varArgs, arrayType));
        return success(args);
      }

      arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
      if (arg.isFail()) return fail(arg, pos, reader);
      else varArgs.add(arg.get());
    }
  }

  private ParseResult<List<Expression>> parseVarArguments1(int pos, CtClass argType, SourceStringReader reader, Environment env) {
    int aPos = reader.getPos();

    // Expression<ArrayType>
    ParseResult<Expression> arg = ExpressionParser.getParser(argType, env).applyRule(reader, env);
    if (! arg.isFail()) return success(Arrays.asList(arg.get()));

    reader.setPos(aPos);
    List<Expression> varArgs = new ArrayList<Expression>();

    final CtClass componentType;
    try {
      componentType = argType.getComponentType();
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      return fail("not found component type of " + argType.getName(), pos, reader);
    }

    // [ Expression<ComponentType> ]
    arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
    if (arg.isFail()) {
      reader.setPos(aPos);
      return success(Arrays.<Expression>asList(new VariableArguments(argType)));
    }
    else varArgs.add(arg.get());

    // { ',' Expression<ComponentType> }
    while (true) {
      int dPos = reader.getPos();
      ParseResult<String> delim = KeywordParser.getParser(",").applyRule(reader, env);
      if (delim.isFail()) {
        reader.setPos(dPos);
        return success(Arrays.<Expression>asList(new VariableArguments(varArgs, argType)));
      }

      arg = ExpressionParser.getParser(componentType, env).applyRule(reader, env);
      if (arg.isFail()) return fail(arg, pos, reader);
      else varArgs.add(arg.get());
    }
  }

  public static ArgumentsParser getParser(CtBehavior behavior) {
    if(! parsers.containsKey(behavior)) parsers.put(behavior, new ArgumentsParser(behavior));
    return parsers.get(behavior);
  }

  private ArgumentsParser(CtBehavior behavior) {
    this.behavior = behavior;
  }

  private final CtBehavior behavior;

  private static Map<CtBehavior, ArgumentsParser> parsers = new HashMap<CtBehavior, ArgumentsParser>();
}
