package proteaj.pparser;

import proteaj.tast.*;
import proteaj.env.type.CommonTypes;
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
          map(enclosed("return", expression(returnType), ";"), ReturnStatement::new);
    };
  }

  private static final PackratParser<ReturnStatement> returnVoidStatement =
      map(keywords("return", ";"), s -> new ReturnStatement());

  protected abstract PackratParser<ReturnStatement> getReturnStatementParser();

  private final PackratParser<Statement> ref_SingleStatement = ref(new ParserThunk<Statement>() {
    @Override
    public PackratParser<Statement> evaluate() { return singleStatement; }
  });

  private final PackratParser<Statement> ref_BlockStatement = ref(new ParserThunk<Statement>() {
    @Override
    public PackratParser<Statement> evaluate() {
      return blockStatement;
    }
  });

  private final PackratParser<Block> block =
      map(scope(enclosed("{", rep(ref_BlockStatement), "}")), Block::new);

  private final PackratParser<LocalsDecl.LocalDecl> simpleLocalDecl =
      map(seq(identifier, arrayBrackets), pair -> new LocalsDecl.LocalDecl(pair._1, pair._2));

  private PackratParser<LocalsDecl.LocalDecl> localDeclAndInit (CtClass type) {
    return map(infix(simpleLocalDecl, "=", expression(type)), pair -> new LocalsDecl.LocalDecl(pair._1.name, pair._1.dim, pair._2));
  }

  private PackratParser<LocalsDecl.LocalDecl> localVarDecl (CtClass type) {
    return withEffect(choice(localDeclAndInit(type), simpleLocalDecl), local -> declareLocal(local.name, type));
  }

  private final PackratParser<LocalsDecl> finalLocalsDecl =
      bind(prefix("final", typeName), type -> map(rep1(localVarDecl(type), ","), locals -> new LocalsDecl(true, type, locals)));

  private final PackratParser<LocalsDecl> nonFinalLocalsDecl =
      bind(typeName, type -> map(rep1(localVarDecl(type), ","), locals -> new LocalsDecl(false, type, locals)));

  private final PackratParser<LocalsDecl> localsDecl =
      choice(finalLocalsDecl, nonFinalLocalsDecl);

  private final PackratParser<Expression> ifCondition =
      prefix("if", enclosed("(", expression(CtClass.booleanType), ")"));

  private final PackratParser<IfStatement> simpleIfStatement =
      map(seq(ifCondition, ref_SingleStatement), pair -> new IfStatement(pair._1, pair._2));

  private final PackratParser<IfStatement> ifElseStatement =
      map(seq(ifCondition, ref_SingleStatement, prefix("else", ref_SingleStatement)), triad -> new IfStatement(triad._1, triad._2, triad._3));

  private final PackratParser<IfStatement> ifStatement =
      choice(ifElseStatement, simpleIfStatement);

  private final PackratParser<CaseBlock> defaultBlock =
      map(prefix("default", prefix(":", rep(ref_BlockStatement))), CaseBlock::new);

  private final PackratParser<CaseBlock> charCaseBlock =
      map(prefix("case", expression(CtClass.charType), ":", rep(ref_BlockStatement)), pair -> new CaseBlock(pair._1, pair._2));

  private final PackratParser<List<CaseBlock>> charCaseBlocks =
      enclosed("{", rep1(choice(charCaseBlock, defaultBlock)), "}");

  private final PackratParser<SwitchStatement> switchCharStatement =
      map(seq(prefix("switch", enclosed("(", expression(CtClass.charType), ")")), charCaseBlocks), pair -> new SwitchStatement(pair._1, pair._2));

  private final PackratParser<CaseBlock> intCaseBlock =
      map(prefix("case", expression(CtClass.intType), ":", rep(ref_BlockStatement)), pair -> new CaseBlock(pair._1, pair._2));

  private final PackratParser<List<CaseBlock>> intCaseBlocks =
      enclosed("{", rep1(choice(intCaseBlock, defaultBlock)), "}");

  private final PackratParser<SwitchStatement> switchIntStatement =
      map(seq(prefix("switch", enclosed("(", expression(CtClass.intType), ")")), intCaseBlocks), pair -> new SwitchStatement(pair._1, pair._2));

  private final PackratParser<SwitchStatement> switchStatement =
      choice(switchCharStatement, switchIntStatement);

  private final PackratParser<Expression> whileCondition =
      prefix("while", enclosed("(", expression(CtClass.booleanType), ")"));

  private final PackratParser<WhileStatement> whileStatement =
      map(seq(whileCondition, ref_SingleStatement), pair -> new WhileStatement(pair._1, pair._2));

  private final PackratParser<DoWhileStatement> doWhileStatement =
      map(enclosed("do", seq(ref_SingleStatement, whileCondition), ";"), pair -> new DoWhileStatement(pair._1, pair._2));

  private final PackratParser<ExpressionList> expressionList =
      map(rep(expression(CtClass.voidType), ","), ExpressionList::new);

  private final PackratParser<Expression> forInit =
      choice(localsDecl, expressionList);

  private final PackratParser<Expression> forCond =
      optional(expression(CtClass.booleanType), new BooleanLiteral(true));

  private final PackratParser<ExpressionList> forUpdate = expressionList;

  private final PackratParser<ForStatement> forStatement =
      map(scope(seq(prefix("for", enclosed("(", forInit, ";")), postfix(forCond, ";"), postfix(forUpdate, ")"), ref_SingleStatement)), quad -> new ForStatement(quad._1, quad._2, quad._3, quad._4));

  private final PackratParser<ThrowStatement> throwStatement =
      withEffect(map(enclosed("throw", expression(CommonTypes.getInstance().throwableType), ";"), ThrowStatement::new), s -> throwing(s.e.type));

  private final PackratParser<Block> tryBlock = prefix("try", block);
  private final PackratParser<Block> finallyBlock = prefix("finally", block);

  private final PackratParser<Pair<CtClass, String>> catchTypeAndName =
      withEffect(prefix("catch", enclosed("(", seq(className, identifier), ")")), pair -> declareLocal(pair._2, pair._1));

  private final PackratParser<Pair<CatchBlock, Environment>> catchClause =
      withEffect(newEnvironment(bind(catchTypeAndName, pair -> map(block, block -> new CatchBlock(pair._1, pair._2, block)))), pair -> catching(pair._1.clazz));

  private final PackratParser<Pair<List<CatchBlock>, List<Environment>>> catchClauseLists = unzip(rep1(catchClause));

  private final PackratParser<List<CatchBlock>> catchClauses =
      map(withEffect(catchClauseLists, pair -> throwing(pair._2)), pair -> pair._1);

  private final PackratParser<TryStatement> tryFinallyStatement =
      map(seq(tryBlock, finallyBlock), pair -> new TryStatement(pair._1, pair._2));

  private final PackratParser<TryStatement> tryCatchStatement =
      map(seq(tryBlock, catchClauses), pair -> new TryStatement(pair._1, pair._2));

  private final PackratParser<TryStatement> tryCatchFinallyStatement =
      map(seq(tryBlock, catchClauses, finallyBlock), triad -> new TryStatement(triad._1, triad._2, triad._3));

  private final PackratParser<TryStatement> tryStatement =
      choice(tryCatchFinallyStatement, tryCatchStatement, tryFinallyStatement);

  private final PackratParser<BreakStatement> breakStatement =
      map(keywords("break", ";"), ss -> new BreakStatement());

  private final PackratParser<ContinueStatement> continueStatement =
      map(keywords("continue", ";"), ss -> new ContinueStatement());

  private final PackratParser<ReturnStatement> returnStatement =
      ref(new ParserThunk<ReturnStatement>() {
        @Override
        public PackratParser<ReturnStatement> evaluate() { return getReturnStatementParser(); }
      });

  private final PackratParser<SynchronizedStatement> syncStatement =
      map(prefix("synchronized", seq(enclosed("(", expression(CommonTypes.getInstance().objectType), ")"), block)), pair -> new SynchronizedStatement(pair._1, pair._2));

  private final PackratParser<Statement> controlFlow =
      choice(ifStatement, switchStatement, whileStatement, doWhileStatement, forStatement, throwStatement, tryStatement,
          breakStatement, continueStatement, returnStatement, syncStatement);

  private final PackratParser<Statement> localVarDeclStatement =
      map(postfix(localsDecl, ";"), LocalsDeclStatement::new);

  private final PackratParser<Statement> expressionStatement =
      map(postfix(expression(CtClass.voidType), ";"), ExpressionStatement::new);

  private final PackratParser<Statement> singleStatement =
      choice(block, controlFlow, expressionStatement);

  private final PackratParser<Statement> blockStatement =
      choice(block, controlFlow, localVarDeclStatement, expressionStatement);

  private final PackratParser<ThisConstructorCall> thisConstructorCall_Args =
      depends(env -> foreach(env.thisClass.getDeclaredConstructors(), c -> bind(arguments(c), args -> {
        if (c == env.thisMember) return failure("recursive constructor invocation");
        else return unit(new ThisConstructorCall(c, args));
      }), "suitable constructor is not found"));

  private final PackratParser<ThisConstructorCall> thisConstructorCall =
      withEffect(enclosed("this", thisConstructorCall_Args, ";"), e -> throwing(e.constructor));

  private final PackratParser<SuperConstructorCall> superConstructorCall_Args =
      depends(env -> {
        CtClass sup;
        try { sup = env.thisClass.getSuperclass(); } catch (NotFoundException e) { return error(e); }
        return foreach(sup.getConstructors(), c -> map(arguments(c), args -> new SuperConstructorCall(c, args)), "suitable super constructor is not found");
      });

  private final PackratParser<SuperConstructorCall> superConstructorCall =
      withEffect(enclosed("super", superConstructorCall_Args, ";"), e -> throwing(e.constructor));

  private final PackratParser<SuperConstructorCall> defaultConstructorCall =
      depends(env -> {
        try {
          CtClass superCls = env.thisClass.getSuperclass();
          if(! hasDefaultConstructor(superCls))
            return failure("implicit super constructor is undefined. Must explicitly invoke another constructor");
          else if(! getDefaultConstructor(superCls).visibleFrom(env.thisClass))
            return failure("implicit super constructor is not visible. Must explicitly invoke another constructor");
          else
            return unit(new SuperConstructorCall(getDefaultConstructor(superCls)));
        } catch (NotFoundException e) { return error(e); }
      });

  private final PackratParser<Statement> anotherConstructorCall =
      choice(thisConstructorCall, superConstructorCall, defaultConstructorCall);

  private final PackratParser<MethodBody> methodBodyParser =
      map(block, MethodBody::new);

  private final PackratParser<ConstructorBody> constructorBodyParser =
      map(enclosed("{", seq(anotherConstructorCall, rep(blockStatement)), "}"), pair -> {
        List<Statement> list = pair._2;
        list.add(0, pair._1);
        return new ConstructorBody(new Block(list));
      });

  private final PackratParser<ClassInitializer> classInitializerParser =
      map(block, ClassInitializer::new);
}
