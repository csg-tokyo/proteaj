package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class ReadasOperationParser extends PackratParser<Expression> {

  @Override
  protected ParseResult<Expression> parse(SourceStringReader reader, Environment env) {
    final int pos = reader.getPos();
    final int length = pattern.getPatternLength();

    List<Expression> operands = new ArrayList<Expression>(pattern.getOperandsLength());

    for(int i = 0; i < length; i++) {
      if(pattern.isOperator(i)) {
        ParseResult<String> operator = ReadasOperatorParser.getParser(pattern.getOperatorKeyword(i)).applyRule(reader, env);
        if(operator.isFail()) return fail(operator, pos, reader);
      }
      else if(pattern.isOperand(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ReadasOperandParser.getParser(pattern.getOperandType(i), priority, inclusive, env).applyRule(reader, env);
        if(! operand.isFail()) {
          operands.add(operand.get());
          continue;
        }

        if(pattern.isVariableOperands(i) || pattern.hasMoreThanOneOperands(i)) try {
          reader.setPos(opos);
          CtClass arrayType = pattern.getOperandType(i);
          CtClass componentType = arrayType.getComponentType();
          List<Expression> args = new ArrayList<Expression>();

          operand = ReadasOperandParser.getParser(componentType, priority, inclusive, env).applyRule(reader, env);

          if (operand.isFail() && pattern.hasMoreThanOneOperands(i)) return fail(operand, pos, reader);
          else if (operand.isFail()) {
            reader.setPos(opos);
            operands.add(new VariableOperands(args, arrayType));
            continue;
          }
          else args.add(operand.get());

          while(true) {
            opos = reader.getPos();
            if(pattern.hasSeparator(i)) {
              if(ReadasOperatorParser.getParser(pattern.getSeparator(i)).applyRule(reader, env).isFail()) break;
            }

            operand = ReadasOperandParser.getParser(componentType, priority, inclusive, env).applyRule(reader, env);
            if(operand.isFail()) break;
            else args.add(operand.get());
          }

          reader.setPos(opos);
          operands.add(new VariableOperands(args, arrayType));
          continue;
        } catch (NotFoundException e) {
          return fail("not found component type of " + pattern.getOperandType(i).getName(), pos, reader);
        }

        else if(pattern.isOptionOperand(i)) try {
          reader.setPos(opos);
          StaticMethodCall mcall = new StaticMethodCall(pattern.getDefaultMethod(i), Arguments.EMPTY_ARGS);
          operands.add(mcall);
          continue;
        } catch (NotFoundException e) {
          return fail("not found type : " + pattern.getOperandType(i).getName(), pos, reader);
        }

        return fail(operand, pos, reader);
      }
      else if(pattern.isAndPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ReadasOperandParser.getParser(pattern.getAndPredicateType(i), priority, inclusive, env).applyRule(reader, env);
        if(operand.isFail()) return fail(operand, pos, reader);
        else reader.setPos(opos);  // continue
      }
      else if(pattern.isNotPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ReadasOperandParser.getParser(pattern.getNotPredicateType(i), priority, inclusive, env).applyRule(reader, env);
        if(operand.isFail()) reader.setPos(opos);          // continue
        else return fail("invalid operand", pos, reader);
      }
    }

    IROperator operator = env.getReadasOperator(type, priority, pattern);
    try {
      env.addExceptions(operator.getExceptionTypes(), reader.getLine());
    } catch (NotFoundException e) {
      return fail("not found exception types of " + operator.getMethodName(), pos, reader);
    }

    return success(new Operation(operator, operands));
  }

  public static ReadasOperationParser getParser(CtClass type, int priority, IRPattern pattern) {
    if(pattern.isDummy()) return PrimitiveReadasOperationParser.getParser(type);

    Triad<CtClass, Integer, IRPattern> key = new Triad<CtClass, Integer, IRPattern>(type, priority, pattern);
    if(parsers.containsKey(key)) return parsers.get(key);

    ReadasOperationParser parser = new ReadasOperationParser(type, pattern, priority);

    parsers.put(key, parser);
    return parser;
  }

  @Override
  public String toString() {
    return "ReadasOperationParser" + "[" + type.getName() + " : " + pattern + "]";
  }

  protected ReadasOperationParser(CtClass type, IRPattern pattern, int priority) {
    this.type = type;
    this.pattern = pattern;
    this.priority = priority;
  }

  protected CtClass type;
  private IRPattern pattern;
  private int priority;

  private static Map<Triad<CtClass, Integer, IRPattern>, ReadasOperationParser> parsers = new HashMap<Triad<CtClass, Integer, IRPattern>, ReadasOperationParser>();
}

