package proteaj.pparser;

import proteaj.ir.*;
import proteaj.tast.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

import static proteaj.pparser.PackratParserCombinators.*;
import static proteaj.pparser.CommonParsers.*;
import static proteaj.pparser.ExpressionParsers.*;
import static proteaj.util.CtClassUtil.*;

public abstract class StatementParsers {
  public static PackratParser<MethodBody> methodBody (final CtClass returnType) {
    return makeMethodBodyParsers(returnType).methodBodyParser;
  }

  public static PackratParser<ConstructorBody> constructorBody () {
    return constructorBodyParsers.constructorBodyParser;
  }

  public static PackratParser<ClassInitializer> classInitializer () {
    return constructorBodyParsers.classInitializerParser;
  }

  private static StatementParsers constructorBodyParsers =
    new StatementParsers() {
      @Override
      protected PackratParser<ReturnStatement> getReturnStatementParser() {
        return failure("return statement cannot be used here");
      }
    };

  private static StatementParsers makeMethodBodyParsers (final CtClass returnType) {
    if (returnType == CtClass.voidType) return new StatementParsers() {
      @Override
      protected PackratParser<ReturnStatement> getReturnStatementParser() {
        return returnVoidStatement;
      }
    };
    else return new StatementParsers() {
      @Override
      protected PackratParser<ReturnStatement> getReturnStatementParser() {
        return returnStatementParser;
      }
      private final PackratParser<ReturnStatement> returnStatementParser =
          map(enclosed("return", expression(returnType), ";"), new Function<Expression, ReturnStatement>() {
            @Override
            public ReturnStatement apply(Expression expr) { return new ReturnStatement(expr); }
          });
    };
  }

  private static final PackratParser<ReturnStatement> returnVoidStatement =
      map(keywords("return", ";"), new Function<String[], ReturnStatement>() {
        @Override
        public ReturnStatement apply(String[] s) { return new ReturnStatement(); }
      });

  protected abstract PackratParser<ReturnStatement> getReturnStatementParser();

  private final PackratParser<Statement> ref_SingleStatement = ref(new ParserThunk<Statement>() {
    @Override
    public PackratParser<Statement> evaluate() { return singleStatement; }
  });

  private final PackratParser<Block> block =
      map(scope(enclosed("{", rep(ref(new ParserThunk<Statement>() {
        @Override
        public PackratParser<Statement> evaluate() {
          return blockStatement;
        }
      })), "}")),
          new Function<List<Statement>, Block>() {
        @Override
        public Block apply(List<Statement> statements) {
          return new Block(statements);
        }
      });

  private final PackratParser<LocalVarDecl> simpleLocalDecl =
      map(seq(typeName, identifier),
          new Function<Pair<CtClass, String>, LocalVarDecl>() {
            @Override
            public LocalVarDecl apply(Pair<CtClass, String> pair) {
              return new LocalVarDecl(pair._1, pair._2);
            }
          });

  private final PackratParser<LocalVarDecl> localDeclAndInit =
      bind(postfix(seq(typeName, identifier), "="), new Function<Pair<CtClass, String>, PackratParser<LocalVarDecl>>() {
        @Override
        public PackratParser<LocalVarDecl> apply(final Pair<CtClass, String> pair) {
          return map(expression(pair._1), new Function<Expression, LocalVarDecl>() {
            @Override
            public LocalVarDecl apply(Expression expr) {return new LocalVarDecl(pair._1, pair._2, expr);
            }
          });
        }
      });

  private final PackratParser<LocalVarDecl> localVarDecl =
      withEffect(choice(localDeclAndInit, simpleLocalDecl),
          new Function<LocalVarDecl, Effect>() {
            @Override
            public Effect apply(LocalVarDecl local) {
              return declareLocal(local.name, local.type);
            }
          });

  private final PackratParser<Expression> ifCondition =
      prefix("if", enclosed("(", expression(CtClass.booleanType), ")"));

  private final PackratParser<IfStatement> simpleIfStatement =
      map(seq(ifCondition, ref_SingleStatement),
          new Function<Pair<Expression, Statement>, IfStatement>() {
            @Override
            public IfStatement apply(Pair<Expression, Statement> pair) {
              return new IfStatement(pair._1, pair._2);
            }
          });

  private final PackratParser<IfStatement> ifElseStatement =
      map(seq(ifCondition, ref_SingleStatement, prefix("else", ref_SingleStatement)),
          new Function<Triad<Expression, Statement, Statement>, IfStatement>() {
            @Override
            public IfStatement apply(Triad<Expression, Statement, Statement> triad) {
              return new IfStatement(triad._1, triad._2, triad._3);
            }
          });

  private final PackratParser<IfStatement> ifStatement =
      choice(ifElseStatement, simpleIfStatement);

  private final PackratParser<Expression> whileCondition =
      prefix("while", enclosed("(", expression(CtClass.booleanType), ")"));

  private final PackratParser<WhileStatement> whileStatement =
      map(seq(whileCondition, ref_SingleStatement),
          new Function<Pair<Expression, Statement>, WhileStatement>() {
            @Override
            public WhileStatement apply(Pair<Expression, Statement> pair) {
              return new WhileStatement(pair._1, pair._2);
            }
          });

  private final PackratParser<DoWhileStatement> doWhileStatement =
      map(enclosed("do", seq(ref_SingleStatement, whileCondition), ";"),
          new Function<Pair<Statement, Expression>, DoWhileStatement>() {
            @Override
            public DoWhileStatement apply(Pair<Statement, Expression> pair) {
              return new DoWhileStatement(pair._1, pair._2);
            }
          });

  private final PackratParser<ExpressionList> expressionList =
      map(rep(expression(CtClass.voidType), ","),
          new Function<List<Expression>, ExpressionList>() {
            @Override
            public ExpressionList apply(List<Expression> expressions) {
              return new ExpressionList(expressions);
            }
          });

  private final PackratParser<Expression> forInit =
      choice(localVarDecl, expressionList);

  private final PackratParser<Expression> forCond =
      optional(expression(CtClass.booleanType), new BooleanLiteral(true));

  private final PackratParser<ExpressionList> forUpdate = expressionList;

  private final PackratParser<ForStatement> forStatement =
      map(scope(seq(prefix("for", enclosed("(", forInit, ";")), postfix(forCond, ";"), postfix(forUpdate, ")"), ref_SingleStatement)),
          new Function<Quad<Expression, Expression, ExpressionList, Statement>, ForStatement>() {
            @Override
            public ForStatement apply(Quad<Expression, Expression, ExpressionList, Statement> quad) {
              return new ForStatement(quad._1, quad._2, quad._3, quad._4);
            }
          });

  private final PackratParser<ThrowStatement> throwStatement =
      withEffect(map(enclosed("throw", expression(IRCommonTypes.getThrowableType()), ";"),
          new Function<Expression, ThrowStatement>() {
            @Override
            public ThrowStatement apply(Expression expr) {
              return new ThrowStatement(expr);
            }
          }), new Function<ThrowStatement, Effect>() {
        @Override
        public Effect apply(ThrowStatement throwStatement) {
          return throwing(throwStatement.e.getType());
        }
      });

  private final PackratParser<Block> tryBlock = prefix("try", block);
  private final PackratParser<Block> finallyBlock = prefix("finally", block);

  private final PackratParser<Pair<CtClass, String>> catchTypeAndName =
      withEffect(prefix("catch", enclosed("(", seq(className, identifier), ")")),
          new Function<Pair<CtClass, String>, Effect>() {
            @Override
            public Effect apply(Pair<CtClass, String> pair) {
              return declareLocal(pair._2, pair._1);
            }
          });

  private final PackratParser<Pair<Triad<CtClass,String,Block>, Environment>> catchClause =
      newEnvironment(bind(catchTypeAndName, new Function<Pair<CtClass, String>, PackratParser<Triad<CtClass, String, Block>>>() {
        @Override
        public PackratParser<Triad<CtClass, String, Block>> apply(final Pair<CtClass, String> pair) {
          return map(block, new Function<Block, Triad<CtClass, String, Block>>() {
                @Override
                public Triad<CtClass, String, Block> apply(Block block) { return Triad.make(pair._1, pair._2, block); }
              });
        }
      }));

  private final PackratParser<List<Triad<CtClass,String,Block>>> catchClauses =
      map(withEffect(unzip(rep1(
          withEffect(catchClause, new Function<Pair<Triad<CtClass, String, Block>, Environment>, Effect>() {
            @Override
            public Effect apply(Pair<Triad<CtClass, String, Block>, Environment> pair) {
              return catching(pair._1._1);
            }
          }))),
          new Function<Pair<List<Triad<CtClass, String, Block>>, List<Environment>>, Effect>() {
            @Override
            public Effect apply(Pair<List<Triad<CtClass, String, Block>>, List<Environment>> pair) { return throwing(pair._2); }
          }),
          new Function<Pair<List<Triad<CtClass, String, Block>>, List<Environment>>, List<Triad<CtClass, String, Block>>>() {
            @Override
            public List<Triad<CtClass, String, Block>> apply(Pair<List<Triad<CtClass, String, Block>>, List<Environment>> pair) {
              return pair._1;
            }
          });

  private final PackratParser<TryStatement> tryFinallyStatement =
      map(seq(tryBlock, finallyBlock), new Function<Pair<Block, Block>, TryStatement>() {
        @Override
        public TryStatement apply(Pair<Block, Block> pair) {
          return new TryStatement(pair._1, pair._2);
        }
      });

  private final PackratParser<TryStatement> tryCatchStatement =
      map(seq(tryBlock, catchClauses), new Function<Pair<Block, List<Triad<CtClass, String, Block>>>, TryStatement>() {
        @Override
        public TryStatement apply(Pair<Block, List<Triad<CtClass, String, Block>>> pair) {
          return new TryStatement(pair._1, pair._2);
        }
      });

  private final PackratParser<TryStatement> tryCatchFinallyStatement =
      map(seq(tryBlock, catchClauses, finallyBlock), new Function<Triad<Block, List<Triad<CtClass, String, Block>>, Block>, TryStatement>() {
        @Override
        public TryStatement apply(Triad<Block, List<Triad<CtClass, String, Block>>, Block> triad) {
          return new TryStatement(triad._1, triad._2, triad._3);
        }
      });

  private final PackratParser<TryStatement> tryStatement =
      choice(tryCatchFinallyStatement, tryCatchStatement, tryFinallyStatement);

  private final PackratParser<ReturnStatement> returnStatement =
      ref(new ParserThunk<ReturnStatement>() {
        @Override
        public PackratParser<ReturnStatement> evaluate() { return getReturnStatementParser(); }
      });

  private final PackratParser<Statement> controlFlow =
      choice(ifStatement, whileStatement, doWhileStatement,
          forStatement, throwStatement, tryStatement, returnStatement);

  private final PackratParser<Statement> localVarDeclStatement =
      map(postfix(localVarDecl, ";"), new Function<LocalVarDecl, Statement>() {
        @Override
        public Statement apply(LocalVarDecl local) {
          return new LocalVarDeclStatement(local);
        }
      });

  private final PackratParser<Statement> expressionStatement =
      map(postfix(expression(CtClass.voidType), ";"), new Function<Expression, Statement>() {
        @Override
        public Statement apply(Expression expr) { return new ExpressionStatement(expr); }
      });

  private final PackratParser<Statement> singleStatement =
      choice(block, controlFlow, expressionStatement);

  private final PackratParser<Statement> blockStatement =
      choice(block, controlFlow, localVarDeclStatement, expressionStatement);

  private PackratParser<ThisConstructorCall> thisConstructorArgs (final Environment env, final CtConstructor constructor) {
    return bind(arguments(constructor), new Function<List<Expression>, PackratParser<ThisConstructorCall>>() {
      @Override
      public PackratParser<ThisConstructorCall> apply(List<Expression> expressions) {
        if (constructor == env.thisMember) return failure("recursive constructor invocation");
        else return effect(unit(new ThisConstructorCall(constructor, expressions)), throwing(constructor));
      }
    });
  }

  private PackratParser<SuperConstructorCall> superConstructorArgs (final CtConstructor constructor) {
    return bind(arguments(constructor), new Function<List<Expression>, PackratParser<SuperConstructorCall>>() {
      @Override
      public PackratParser<SuperConstructorCall> apply(List<Expression> expressions) {
        return effect(unit(new SuperConstructorCall(constructor, expressions)), throwing(constructor));
      }
    });
  }

  private final PackratParser<ThisConstructorCall> thisConstructorCall =
      enclosed("this", depends(new Function<Environment, PackratParser<ThisConstructorCall>>() {
        @Override
        public PackratParser<ThisConstructorCall> apply(final Environment env) {
          return foreach(env.thisClass.getDeclaredConstructors(),
              new Function<CtConstructor, PackratParser<ThisConstructorCall>>() {
            @Override
            public PackratParser<ThisConstructorCall> apply(final CtConstructor c) { return thisConstructorArgs(env, c); }
          },
              "suitable constructor is not found");
        }
      }), ";");

  private final PackratParser<SuperConstructorCall> superConstructorCall =
      enclosed("super", depends(new Function<Environment, PackratParser<SuperConstructorCall>>() {
        @Override
        public PackratParser<SuperConstructorCall> apply(final Environment env) {
          try {
            return foreach(env.thisClass.getSuperclass().getDeclaredConstructors(),
                new Function<CtConstructor, PackratParser<SuperConstructorCall>>() {
              @Override
              public PackratParser<SuperConstructorCall> apply(CtConstructor c) { return superConstructorArgs(c); }
            },
                "suitable super constructor is not found");
          } catch (NotFoundException e) { return error(e); }
        }
      }), ";");

  private final PackratParser<SuperConstructorCall> defaultConstructorCall =
      depends(new Function<Environment, PackratParser<SuperConstructorCall>>() {
        @Override
        public PackratParser<SuperConstructorCall> apply(Environment env) {
          try {
            CtClass superCls = env.thisClass.getSuperclass();
            if(! hasDefaultConstructor(superCls))
              return failure("implicit super constructor is undefined. Must explicitly invoke another constructor");
            else if(! getDefaultConstructor(superCls).visibleFrom(env.thisClass))
              return failure("implicit super constructor is not visible. Must explicitly invoke another constructor");
            else
              return unit(new SuperConstructorCall(getDefaultConstructor(superCls)));
          } catch (NotFoundException e) { return error(e); }
        }
      });

  private final PackratParser<Statement> anotherConstructorCall =
      choice(thisConstructorCall, superConstructorCall, defaultConstructorCall);

  private final PackratParser<MethodBody> methodBodyParser =
      map(block, new Function<Block, MethodBody>() {
        @Override
        public MethodBody apply(Block b) { return new MethodBody(b); }
      });

  private final PackratParser<ConstructorBody> constructorBodyParser =
      map(enclosed("{", seq(anotherConstructorCall, rep(blockStatement)), "}"), new Function<Pair<Statement, List<Statement>>, ConstructorBody>() {
        @Override
        public ConstructorBody apply(Pair<Statement, List<Statement>> pair) {
          List<Statement> list = pair._2;
          list.add(0, pair._1);
          return new ConstructorBody(new Block(list));
        }
      });

  private final PackratParser<ClassInitializer> classInitializerParser =
      map(block, new Function<Block, ClassInitializer>() {
        @Override
        public ClassInitializer apply(Block b) { return new ClassInitializer(b); }
      });
}
