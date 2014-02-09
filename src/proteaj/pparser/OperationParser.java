package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

public class OperationParser extends PackratParser {

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    int length = pattern.getPatternLength();

    List<Expression> operands = new ArrayList<Expression>(pattern.getOperandsLength());

    for(int i = 0; i < length; i++) {
      if(pattern.isOperator(i)) {
        TypedAST operator = KeywordParser.getParser(pattern.getOperatorKeyword(i)).applyRule(reader, env);
        if(operator.isFail()) {
          reader.setPos(pos);
          return new BadAST(operator.getFailLog());
        }
      }
      else if(pattern.isOperand(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        TypedAST operand = ExpressionParser.getParser(pattern.getOperandType(i), priority, inclusive).applyRule(reader, env);
        if(! operand.isFail()) {
          operands.add((Expression)operand);
          continue;
        }

        if(pattern.isVariableOperands(i) || pattern.hasMoreThanOneOperands(i)) try {
          reader.setPos(opos);
          CtClass arrayType = pattern.getOperandType(i);
          CtClass componentType = arrayType.getComponentType();
          List<Expression> args = new ArrayList<Expression>();

          operand = ExpressionParser.getParser(componentType, priority, inclusive).applyRule(reader, env);
          /*if(operand.isFail() && pattern.isReadasOperand(i)) {
            reader.setPos(opos);
            operand = ReadasExpressionParser.getParser(componentType).applyRule(reader, env);
          }*/

          if(operand.isFail()) {
            reader.setPos(opos);

            if(pattern.hasMoreThanOneOperands(i)) {
              return new BadAST(operand.getFailLog());
            }
            else {
              operands.add(new VariableOperands(args, arrayType));
              continue;
            }
          }
          else args.add((Expression)operand);

          if(pattern.hasSeparator(i)) while(true) {
            opos = reader.getPos();
            TypedAST sep = KeywordParser.getParser(pattern.getSeparator(i)).applyRule(reader, env);
            if(sep.isFail()) break;

            //int epos = reader.getPos();
            operand = ExpressionParser.getParser(componentType, priority, inclusive).applyRule(reader, env);
            /*if(operand.isFail() && pattern.isReadasOperand(i)) {
              reader.setPos(epos);
              operand = ReadasExpressionParser.getParser(componentType).applyRule(reader, env);
            }*/

            if(operand.isFail()) break;
            else args.add((Expression)operand);
          }
          else while(true) {
            opos = reader.getPos();
            operand = ExpressionParser.getParser(componentType, priority, inclusive).applyRule(reader, env);
            /*if(operand.isFail() && pattern.isReadasOperand(i)) {
              reader.setPos(opos);
              operand = ReadasExpressionParser.getParser(componentType).applyRule(reader, env);
            }*/

            if(operand.isFail()) break;
            else args.add((Expression)operand);
          }

          reader.setPos(opos);
          operands.add(new VariableOperands(args, arrayType));
          continue;

        } catch (NotFoundException e) {
          FailLog flog = new FailLog("not found component type of " + pattern.getOperandType(i).getName(), reader.getPos(), reader.getLine());
          reader.setPos(pos);
          return new BadAST(flog);
        }

        /*else if(pattern.isReadasOperand(i)) {
          reader.setPos(opos);
          operand = ReadasExpressionParser.getParser(pattern.getOperandType(i)).applyRule(reader, env);

          if(! operand.isFail()) {
            operands.add((Expression)operand);
            continue;
          }
        }*/

        if(pattern.isOptionOperand(i)) try {
          reader.setPos(opos);
          StaticMethodCall mcall = new StaticMethodCall(pattern.getDefaultMethod(i), Arguments.EMPTY_ARGS);
          operands.add(mcall);
          continue;
        } catch (NotFoundException e) {
          FailLog flog = new FailLog("not found type : " + pattern.getOperandType(i).getName(), reader.getPos(), reader.getLine());
          reader.setPos(pos);
          return new BadAST(flog);
        }

        reader.setPos(pos);
        return new BadAST(operand.getFailLog());
      }
      else if(pattern.isAndPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        TypedAST operand = ExpressionParser.getParser(pattern.getAndPredicateType(i), priority, inclusive).applyRule(reader, env);
        if(operand.isFail()) {
          reader.setPos(pos);
          return new BadAST(operand.getFailLog());
        }
        else {
          reader.setPos(opos);
          continue;
        }
      }
      else if(pattern.isNotPredicate(i)) {
        int opos = reader.getPos();
        boolean inclusive = pattern.getInclusive(i);

        TypedAST operand = ExpressionParser.getParser(pattern.getNotPredicateType(i), priority, inclusive).applyRule(reader, env);
        if(operand.isFail()) {
          reader.setPos(opos);
          continue;
        }
        else {
          FailLog flog = new FailLog("invalid operand", reader.getPos(), reader.getLine());
          reader.setPos(pos);
          return new BadAST(flog);
        }
      }
    }

    IROperator operator = usops.getIROperator(type, priority, pattern);
    try {
      env.addExceptions(operator.getExceptionTypes(), reader.getLine());
    } catch (NotFoundException e) {
      FailLog flog = new FailLog("not found exception types of " + operator.getMethodName(), reader.getPos(), reader.getLine());
      reader.setPos(pos);
      return new BadAST(flog);
    }

    return new Operation(operator, operands);
  }

  public static OperationParser getParser(CtClass type, int priority, IRPattern pattern) {
    Triad<CtClass, Integer, IRPattern> key = new Triad<CtClass, Integer, IRPattern>(type, priority, pattern);
    if(parsers.containsKey(key)) return parsers.get(key);

    OperationParser parser = new OperationParser(type, pattern, priority);
    parser.init();
    parsers.put(key, parser);
    return parser;
  }

  public static void initAll(UsingOperators usops, IR ir) {
    OperationParser.ir = ir;
    OperationParser.usops = usops;
    for(OperationParser parser : parsers.values()) {
      parser.init();
    }
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
  private static UsingOperators usops;
  private static IR ir;
}

