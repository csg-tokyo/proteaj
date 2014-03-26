package proteaj.pparser;

import java.util.*;
import javassist.*;

import proteaj.error.NotFoundError;
import proteaj.tast.*;
import proteaj.util.*;

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
      bind(postfix(ref_JavaExpression, "="), new Function<Expression, PackratParser<AssignExpression>>() {
        @Override
        public PackratParser<AssignExpression> apply(final Expression left) {
          return map(expression(left.getType()), new Function<Expression, AssignExpression>() {
            @Override
            public AssignExpression apply(Expression right) {
              return new AssignExpression(left, right);
            }
          });
        }
      });

  private static final PackratParser<ArrayLength> arrayLength =
      bind(postfix(postfix(ref_JavaExpression, "."), "length"), new Function<Expression, PackratParser<ArrayLength>>() {
        @Override
        public PackratParser<ArrayLength> apply(Expression expr) {
          if (expr.getType().isArray()) return unit(new ArrayLength(expr));
          else return failure("not array type");
        }
      });

  private static final PackratParser<MethodCall> methodCall =
      bind(infix(ref_JavaExpression, ".", identifier), new Function<Pair<Expression, String>, PackratParser<MethodCall>>() {
        @Override
        public PackratParser<MethodCall> apply(final Pair<Expression, String> pair) {
          return depends(new Function<Environment, PackratParser<MethodCall>>() {
            @Override
            public PackratParser<MethodCall> apply(Environment env) {
              return foreach(env.getInstanceMethods(pair._1.getType(), pair._2), new Function<CtMethod, PackratParser<MethodCall>>() {
                @Override
                public PackratParser<MethodCall> apply(final CtMethod method) {
                  return effect(bind(arguments(method), new Function<List<Expression>, PackratParser<MethodCall>>() {
                    @Override
                    public PackratParser<MethodCall> apply(List<Expression> args) {
                      try { return unit(new MethodCall(pair._1, method, args)); }
                      catch (NotFoundException e) { return error(e); }
                    }
                  }), throwing(method));
                }
              }, "method " + pair._2 + " is not found in " + pair._1.getType().getName());
            }
          });
        }
      });

  private static final PackratParser<FieldAccess> fieldAccess =
      bind(infix(ref_JavaExpression, ".", identifier), new Function<Pair<Expression, String>, PackratParser<FieldAccess>>() {
        @Override
        public PackratParser<FieldAccess> apply(final Pair<Expression, String> pair) {
          return depends(new Function<Environment, PackratParser<FieldAccess>>() {
            @Override
            public PackratParser<FieldAccess> apply(Environment env) {
              final CtField field;
              try { field = pair._1.getType().getField(pair._2); }
              catch (NotFoundException e) {
                return failure("field " + pair._2 + " is not found in " + pair._1.getType().getName());
              }

              if (env.isVisible(field)) {
                if (! isStatic(field)) try {
                  return unit(new FieldAccess(pair._1, field));
                } catch (NotFoundException e) { return error(e); }
                else return failure("field " + pair._1.getType().getName() + '.' + pair._2 + " is a static field");
              }
              else return failure("field " + pair._1.getType().getName() + '.' + pair._2 + " is not visible from " + env.thisClass.getName());
            }
          });
        }
      });

  private static final PackratParser<Expression> arrayIndex = enclosed("[", expression(CtClass.intType), "]");

  private static final PackratParser<ArrayAccess> arrayAccess =
      bind(seq(ref_JavaExpression, arrayIndex), new Function<Pair<Expression, Expression>, PackratParser<ArrayAccess>>() {
        @Override
        public PackratParser<ArrayAccess> apply(Pair<Expression, Expression> pair) {
          if (pair._1.getType().isArray()) try {
            return unit(new ArrayAccess(pair._1, pair._2));
          } catch (NotFoundException e) { return error(e); }
          else return failure("not array type");
        }
      });

  private static final PackratParser<StaticMethodCall> abbStaticMethodCall =
      bind(identifier, new Function<String, PackratParser<StaticMethodCall>>() {
        @Override
        public PackratParser<StaticMethodCall> apply(final String s) {
          return depends(new Function<Environment, PackratParser<StaticMethodCall>>() {
            @Override
            public PackratParser<StaticMethodCall> apply(Environment env) {
              return foreach(env.getStaticMethods(env.thisClass, s), new Function<CtMethod, PackratParser<StaticMethodCall>>() {
                @Override
                public PackratParser<StaticMethodCall> apply(final CtMethod method) {
                  return effect(bind(arguments(method), new Function<List<Expression>, PackratParser<StaticMethodCall>>() {
                    @Override
                    public PackratParser<StaticMethodCall> apply(List<Expression> args) {
                      try {
                        return unit(new StaticMethodCall(method, args));
                      } catch (NotFoundException e) {
                        return error(e);
                      }
                    }
                  }), throwing(method));
                }
              }, "undefined method: " + s);
            }
          });
        }
      });

  private static final PackratParser<MethodCall> abbInstanceMethodCall =
      bind(identifier, new Function<String, PackratParser<MethodCall>>() {
        @Override
        public PackratParser<MethodCall> apply(final String s) {
          return depends(new Function<Environment, PackratParser<MethodCall>>() {
            @Override
            public PackratParser<MethodCall> apply(final Environment env) {
              return foreach(env.getInstanceMethods(env.thisClass, s), new Function<CtMethod, PackratParser<MethodCall>>() {
                @Override
                public PackratParser<MethodCall> apply(final CtMethod method) {
                  return effect(bind(arguments(method), new Function<List<Expression>, PackratParser<MethodCall>>() {
                    @Override
                    public PackratParser<MethodCall> apply(List<Expression> args) {
                      if (env.isStatic()) return failure(method.getName() + " is an instance method of this class");
                      else try {
                        return unit(new MethodCall(env.get("this"), method, args));
                      } catch (NotFoundException e) {
                        return error(e);
                      }
                    }
                  }), throwing(method));
                }
              }, "undefined method: " + s);
            }
          });
        }
      });

  private static final PackratParser<Expression> abbMethodCall =
    choice(abbStaticMethodCall, abbInstanceMethodCall);

  private static final PackratParser<Expression> variable =
      bind(identifier, new Function<String, PackratParser<Expression>>() {
        @Override
        public PackratParser<Expression> apply(final String s) {
          return depends(new Function<Environment, PackratParser<Expression>>() {
            @Override
            public PackratParser<Expression> apply(Environment env) {
              if (env.contains(s)) return unit(env.get(s));
              else return failure("unknown variable: " + s);
            }
          });
        }
      });

  private static final PackratParser<StaticMethodCall> staticMethodCall =
      bind(infix(className, ".", identifier), new Function<Pair<CtClass, String>, PackratParser<StaticMethodCall>>() {
        @Override
        public PackratParser<StaticMethodCall> apply(final Pair<CtClass, String> pair) {
          return depends(new Function<Environment, PackratParser<StaticMethodCall>>() {
            @Override
            public PackratParser<StaticMethodCall> apply(Environment env) {
              return foreach(env.getStaticMethods(pair._1, pair._2), new Function<CtMethod, PackratParser<StaticMethodCall>>() {
                @Override
                public PackratParser<StaticMethodCall> apply(final CtMethod method) {
                  return effect(bind(arguments(method), new Function<List<Expression>, PackratParser<StaticMethodCall>>() {
                    @Override
                    public PackratParser<StaticMethodCall> apply(List<Expression> expr) {
                      try { return unit(new StaticMethodCall(method, expr)); }
                      catch (NotFoundException e) { return error(e); }
                    }
                  }), throwing(method));
                }
              }, "suitable static method is not found");
            }
          });
        }
      });

  private static final PackratParser<StaticFieldAccess> staticFieldAccess =
      bind(infix(className, ".", identifier), new Function<Pair<CtClass, String>, PackratParser<StaticFieldAccess>>() {
        @Override
        public PackratParser<StaticFieldAccess> apply(final Pair<CtClass, String> pair) {
          return depends(new Function<Environment, PackratParser<StaticFieldAccess>>() {
            @Override
            public PackratParser<StaticFieldAccess> apply(Environment env) {
              final CtField field;
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
            }
          });
        }
      });

  private static final PackratParser<NewExpression> newObject =
      bind(prefix("new", className), new Function<CtClass, PackratParser<NewExpression>>() {
        @Override
        public PackratParser<NewExpression> apply(CtClass clazz) {
          return foreach(clazz.getDeclaredConstructors(), new Function<CtConstructor, PackratParser<NewExpression>>() {
            @Override
            public PackratParser<NewExpression> apply(final CtConstructor c) {
              return effect(map(arguments(c), new Function<List<Expression>, NewExpression>() {
                @Override
                public NewExpression apply(List<Expression> args) { return new NewExpression(c, args); }
              }), throwing(c));
            }
          }, "suitable constructor is not found");
        }
      });

  private static final PackratParser<NewArrayExpression> newArray =
      bind(seq(prefix("new", className), rep1(arrayIndex), rep(keywords("[", "]"))),
          new Function<Triad<CtClass, List<Expression>, List<String[]>>, PackratParser<NewArrayExpression>>() {
            @Override
            public PackratParser<NewArrayExpression> apply(final Triad<CtClass, List<Expression>, List<String[]>> triad) { return depends(new Function<Environment, PackratParser<NewArrayExpression>>() {
              @Override
              public PackratParser<NewArrayExpression> apply(Environment env) {
                try {
                  return unit(new NewArrayExpression(env.getArrayType(triad._1, triad._2.size() + triad._3.size()), triad._2));
                } catch (NotFoundError e) { return error(e); }
              }
            });
            }
          });

  private static final PackratParser<CastExpression> cast =
      bind(seq(enclosed("(", typeName, ")"), ref_JavaExpression), new Function<Pair<CtClass, Expression>, PackratParser<CastExpression>>() {
        @Override
        public PackratParser<CastExpression> apply(Pair<CtClass, Expression> pair) {
          try {
            if (isCastable(pair._2.getType(), pair._1)) return unit(new CastExpression(pair._1, pair._2));
            else return failure(pair._2.getType().getName() + " cannot cast to " + pair._1.getName());
          } catch (NotFoundException e) { return error(e); }
        }
      });

  private static final PackratParser<Expression> parenthesized =
      enclosed("(", ref_JavaExpression, ")");

  private static final PackratParser<IntLiteral> intLiteral =
      map(integer, new Function<Integer, IntLiteral>() {
        @Override
        public IntLiteral apply(Integer value) { return new IntLiteral(value); }
      });

  private static final PackratParser<BooleanLiteral> trueLiteral =
      map(keyword("true"), new Function<String, BooleanLiteral>() {
        @Override
        public BooleanLiteral apply(String s) { return new BooleanLiteral(true); }
      });

  private static final PackratParser<BooleanLiteral> falseLiteral =
      map(keyword("false"), new Function<String, BooleanLiteral>() {
        @Override
        public BooleanLiteral apply(String s) { return new BooleanLiteral(false); }
      });

  private static final PackratParser<BooleanLiteral> booleanLiteral =
      choice(trueLiteral, falseLiteral);

  private static final PackratParser<StringLiteral> stringLiteral =
      map(string, new Function<String, StringLiteral>() {
        @Override
        public StringLiteral apply(String s) { return new StringLiteral(s); }
      });

  private static final PackratParser<CharLiteral> charLiteral =
      map(character, new Function<Character, CharLiteral>() {
        @Override
        public CharLiteral apply(Character ch) {
          return new CharLiteral(ch);
        }
      });

  private static final PackratParser<Expression> literal =
      choice(intLiteral, booleanLiteral, stringLiteral, charLiteral);

  private static final PackratParser<Expression> primary =
      choice(abbMethodCall, variable, staticMethodCall, staticFieldAccess, newObject, newArray, cast, parenthesized, literal);

  public static final PackratParser<Expression> javaExpression =
      choice(assignment, arrayLength, methodCall, fieldAccess, arrayAccess, primary);
}
