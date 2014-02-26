package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class OperationParser extends PackratParser<Operation> {

  @Override
  protected ParseResult<Operation> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    int length = pattern.getPatternLength();

    List<Expression> operands = new ArrayList<Expression>(pattern.getOperandsLength());

    for(int i = 0; i < length; i++) {
      if(pattern.isOperator(i)) {
        ParseResult<String> operator = KeywordParser.getParser(pattern.getOperatorKeyword(i)).applyRule(reader, env);
        if(operator.isFail()) return fail(operator, pos, reader);
      }
      else if(pattern.isOperand(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ExpressionParser.getParser(pattern.getOperandType(i), priority, inclusive, env).applyRule(reader, env);
        if(! operand.isFail()) {
          operands.add(operand.get());
          continue;
        }

        /* '*' and '+' */
        if(pattern.isVariableOperands(i) || pattern.hasMoreThanOneOperands(i)) try {
          reader.setPos(opos);
          CtClass arrayType = pattern.getOperandType(i);
          CtClass componentType = arrayType.getComponentType();
          List<Expression> args = new ArrayList<Expression>();

          operand = ExpressionParser.getParser(componentType, priority, inclusive, env).applyRule(reader, env);

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
              if(KeywordParser.getParser(pattern.getSeparator(i)).applyRule(reader, env).isFail()) break;
            }

            operand = ExpressionParser.getParser(componentType, priority, inclusive, env).applyRule(reader, env);
            if(operand.isFail()) break;
            else args.add(operand.get());
          }

          reader.setPos(opos);
          operands.add(new VariableOperands(args, arrayType));
          continue;

        } catch (NotFoundException e) {
          return fail("not found component type of " + pattern.getOperandType(i).getName(), pos, reader);
        }

        /* '?' */
        else if(pattern.isOptionOperand(i)) try {
          reader.setPos(opos);
          StaticMethodCall mcall = new StaticMethodCall(pattern.getDefaultMethod(i), Arguments.EMPTY_ARGS);
          operands.add(mcall);
          continue;
        } catch (NotFoundException e) {
          return fail("not found type : " + pattern.getOperandType(i).getName(), pos, reader);
        }

        /* else */
        return fail(operand, pos, reader);
      }
      else if(pattern.isAndPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ExpressionParser.getParser(pattern.getAndPredicateType(i), priority, inclusive, env).applyRule(reader, env);
        if(operand.isFail()) return fail(operand, pos, reader);
        else reader.setPos(opos);   // continue
      }
      else if(pattern.isNotPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        ParseResult<Expression> operand = ExpressionParser.getParser(pattern.getNotPredicateType(i), priority, inclusive, env).applyRule(reader, env);
        if(operand.isFail()) reader.setPos(opos);          // continue
        else return fail("invalid operand", pos, reader);
      }
    }

    IROperator operator = env.getOperator(type, priority, pattern);
    try {
      env.addExceptions(operator.getExceptionTypes(), reader.getLine());
    } catch (NotFoundException e) {
      return fail("not found exception types of " + operator.getMethodName(), pos, reader);
    }

    return success(new Operation(operator, operands));
  }

  public static OperationParser getParser(CtClass type, int priority, IRPattern pattern) {
    Triad<CtClass, Integer, IRPattern> key = new Triad<CtClass, Integer, IRPattern>(type, priority, pattern);
    if(parsers.containsKey(key)) return parsers.get(key);

    OperationParser parser = new OperationParser(type, pattern, priority);
    parsers.put(key, parser);
    return parser;
  }

  @Override
  public String toString() {
    return "OperationParser" + "[" + type.getName() + " : " + pattern + "]";
  }

  private OperationParser(CtClass type, IRPattern pattern, int priority) {
    this.type = type;
    this.pattern = pattern;
    this.priority = priority;
  }

  private CtClass type;
  private IRPattern pattern;
  private int priority;

  private static Map<Triad<CtClass, Integer, IRPattern>, OperationParser> parsers = new HashMap<Triad<CtClass, Integer, IRPattern>, OperationParser>();
}

