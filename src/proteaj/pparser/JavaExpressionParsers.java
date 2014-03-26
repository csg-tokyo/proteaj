package proteaj.pparser;

import javassist.*;

import proteaj.error.*;
import proteaj.tast.*;

import static proteaj.pparser.PackratParserCombinators.*;
import static proteaj.pparser.CommonParsers.*;
import static proteaj.pparser.ExpressionParsers.*;
import static proteaj.util.CtClassUtil.*;
import static proteaj.util.Modifiers.*;

public class JavaExpressionParsers {
  private static final PackratParser<Expression> ref_JavaExpression = ref(new ParserThunk<Expression>() {
    @Override
    public PackratParser<Expression> evaluate() { return javaExpression; }
  });

  private static final PackratParser<AssignExpression> assignment =
      bind(postfix(ref_JavaExpression, "="), left -> map(expression(left.type), right -> new AssignExpression(left, right)));

  private static final PackratParser<ArrayLength> arrayLength =
      bind(postfix(postfix(ref_JavaExpression, "."), "length"), expr -> {
        if (expr.type.isArray()) return unit(new ArrayLength(expr));
        else return failure("not array type");
      });

  private static final PackratParser<MethodCall> methodCall =
      bind(infix(ref_JavaExpression, ".", identifier), pair -> depends(env -> foreach(env.getInstanceMethods(pair._1.type, pair._2), method -> methodCallArgs(pair._1, method), "method " + pair._2 + " is not found in " + pair._1.type.getName())));

  private static PackratParser<MethodCall> methodCallArgs (Expression receiver, CtMethod method) {
    return effect(bind(arguments(method), args -> {
      try { return unit(new MethodCall(receiver, method, args)); }
      catch (NotFoundException e) { return error(e); }
    }), throwing(method));
  }

  private static final PackratParser<FieldAccess> fieldAccess =
      bind(infix(ref_JavaExpression, ".", identifier), pair -> depends(env -> {
        CtField field;
        try { field = pair._1.type.getField(pair._2); }
        catch (NotFoundException e) {
          return failure("field " + pair._2 + " is not found in " + pair._1.type.getName());
        }

        if (env.isVisible(field)) {
          if (! isStatic(field)) try {
            return unit(new FieldAccess(pair._1, field));
          } catch (NotFoundException e) { return error(e); }
          else return failure("field " + pair._1.type.getName() + '.' + pair._2 + " is a static field");
        }
        else return failure("field " + pair._1.type.getName() + '.' + pair._2 + " is not visible from " + env.thisClass.getName());
      }));

  private static final PackratParser<Expression> arrayIndex = enclosed("[", expression(CtClass.intType), "]");

  private static final PackratParser<ArrayAccess> arrayAccess =
      bind(seq(ref_JavaExpression, arrayIndex), pair -> {
        if (pair._1.type.isArray()) try {
          return unit(new ArrayAccess(pair._1, pair._2));
        } catch (NotFoundException e) { return error(e); }
        else return failure("not array type");
      });

  private static final PackratParser<StaticMethodCall> abbStaticMethodCall =
      bind(identifier, s -> depends(env -> foreach(env.getStaticMethods(env.thisClass, s), method -> staticMethodCallArgs(method), "undefined method: " + s)));

  private static PackratParser<StaticMethodCall> staticMethodCallArgs (CtMethod method) {
    return effect(bind(arguments(method), args -> {
      try { return unit(new StaticMethodCall(method, args)); }
      catch (NotFoundException e) { return error(e); }
    }), throwing(method));
  }

  private static final PackratParser<MethodCall> abbInstanceMethodCall =
      bind(identifier, s -> depends(env -> env.isStatic() ? failure("cannot abbreviate a receiver of an instance method") : foreach(env.getInstanceMethods(env.thisClass, s), method -> methodCallArgs(env.get("this"), method), "undefined method: " + s)));

  private static final PackratParser<Expression> abbMethodCall =
      choice(abbStaticMethodCall, abbInstanceMethodCall);

  private static final PackratParser<Expression> variable =
      bind(identifier, s -> depends(env -> env.contains(s) ? unit(env.get(s)) : failure("unknown variable: " + s)));

  private static final PackratParser<StaticMethodCall> staticMethodCall =
      bind(infix(className, ".", identifier), pair -> depends(env -> foreach(env.getStaticMethods(pair._1, pair._2), method -> staticMethodCallArgs(method), "suitable static method is not found")));

  private static final PackratParser<StaticFieldAccess> staticFieldAccess =
      bind(infix(className, ".", identifier), pair -> depends(env -> {
        CtField field;
        try { field = pair._1.getField(pair._2); }
        catch (NotFoundException e) {
          return failure("field " + pair._2 + " is not found in " + pair._1.getName());
        }

        if (env.isVisible(field)) {
          if (isStatic(field)) try {
            return unit(new StaticFieldAccess(field));
          } catch (NotFoundException e) { return error(e); }
          else return failure("field " + pair._1.getName() + '.' + pair._2 + " is not a static field");
        }
        else return failure("field " + pair._1.getName() + '.' + pair._2 + " is not visible from " + env.thisClass.getName());
      }));

  private static final PackratParser<NewExpression> newObject =
      bind(prefix("new", className), clazz -> foreach(clazz.getDeclaredConstructors(), c -> effect(map(arguments(c), args -> new NewExpression(c, args)), throwing(c)), "suitable constructor is not found"));

  private static final PackratParser<NewArrayExpression> newArray =
      bind(seq(prefix("new", className), rep1(arrayIndex), rep(keywords("[", "]"))), triad -> depends(env -> {
        int dim = triad._2.size() + triad._3.size();
        CtClass arrayType;
        try { arrayType = env.getArrayType(triad._1, dim); } catch (NotFoundError e) { return error(e); }
        return unit(new NewArrayExpression(arrayType, triad._2));
      }));

  private static final PackratParser<CastExpression> cast =
      bind(seq(enclosed("(", typeName, ")"), ref_JavaExpression), pair -> {
        try {
          if (isCastable(pair._2.type, pair._1)) return unit(new CastExpression(pair._1, pair._2));
          else return failure(pair._2.type.getName() + " cannot cast to " + pair._1.getName());
        } catch (NotFoundException e) { return error(e); }
      });

  private static final PackratParser<Expression> parenthesized =
      enclosed("(", ref_JavaExpression, ")");

  private static final PackratParser<IntLiteral> intLiteral = map(integer, IntLiteral::new);

  private static final PackratParser<BooleanLiteral> trueLiteral = map(keyword("true"), s -> new BooleanLiteral(true));

  private static final PackratParser<BooleanLiteral> falseLiteral = map(keyword("false"), s -> new BooleanLiteral(false));

  private static final PackratParser<BooleanLiteral> booleanLiteral = choice(trueLiteral, falseLiteral);

  private static final PackratParser<StringLiteral> stringLiteral = map(string, StringLiteral::new);

  private static final PackratParser<CharLiteral> charLiteral = map(character, CharLiteral::new);

  private static final PackratParser<Expression> literal =
      choice(intLiteral, booleanLiteral, stringLiteral, charLiteral);

  private static final PackratParser<Expression> primary =
      choice(abbMethodCall, variable, staticMethodCall, staticFieldAccess, newObject, newArray, cast, parenthesized, literal);

  public static final PackratParser<Expression> javaExpression =
      choice(assignment, arrayLength, methodCall, fieldAccess, arrayAccess, primary);
}
