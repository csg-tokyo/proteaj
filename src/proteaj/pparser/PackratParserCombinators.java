package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.type.CommonTypes;
import proteaj.util.*;

import java.util.*;
import java.util.function.*;
import javassist.*;

class PackratParserCombinators {
  public static <T> PackratParser<T> prefix (final PackratParser<?> prefix, final PackratParser<T> parser) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<?> prefixResult = prefix.applyRule(reader, env);
        if (prefixResult.isFail()) return fail(prefixResult, pos, reader);

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        return result;
      }
    };
  }

  public static <T> PackratParser<T> postfix (final PackratParser<T> parser, final PackratParser<?> postfix) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        ParseResult<?> postfixResult = postfix.applyRule(reader, env);
        if (postfixResult.isFail()) return fail(postfixResult, pos, reader);

        return result;
      }
    };
  }

  public static <T> PackratParser<T> enclosed (final PackratParser<?> prefix, final PackratParser<T> parser, final PackratParser<?> postfix) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<?> prefixResult = prefix.applyRule(reader, env);
        if (prefixResult.isFail()) return fail(prefixResult, pos, reader);

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        ParseResult<?> postfixResult = postfix.applyRule(reader, env);
        if (postfixResult.isFail()) return fail(postfixResult, pos, reader);

        return result;
      }
    };
  }

  public static <T1, T2> PackratParser<Pair<T1, T2>> infix (final PackratParser<T1> parser1, final PackratParser<?> infix, final PackratParser<T2> parser2) {
    return new PackratParser<Pair<T1, T2>>() {
      @Override
      protected ParseResult<Pair<T1, T2>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<T1> result1 = parser1.applyRule(reader, env);
        if (result1.isFail()) return fail(result1, pos, reader);

        ParseResult<?> infixResult = infix.applyRule(reader, env);
        if (infixResult.isFail()) return fail(infixResult, pos, reader);

        ParseResult<T2> result2 = parser2.applyRule(reader, env);
        if (result2.isFail()) return fail(result2, pos, reader);

        return success(Pair.make(result1.get(), result2.get()));
      }
    };
  }

  public static <T> PackratParser<T> optional (final PackratParser<T> parser, final T defaultValue) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) {
          reader.setPos(pos);
          return success(defaultValue);
        }
        else return result;
      }
    };
  }

  public static <T> PackratParser<List<T>> rep (final PackratParser<T> parser) {
    return new PackratParser<List<T>>() {
      @Override
      protected ParseResult<List<T>> parse(SourceStringReader reader, Environment env) {
        int pos;
        List<T> list = new ArrayList<>();

        while(true) {
          pos = reader.getPos();
          ParseResult<T> result = parser.applyRule(reader, env);
          if (result.isFail()) break;
          else list.add(result.get());
        }

        reader.setPos(pos);
        return success(list);
      }
    };
  }

  public static <T> PackratParser<List<T>> rep1 (final PackratParser<T> parser) {
    return new PackratParser<List<T>>() {
      @Override
      protected ParseResult<List<T>> parse(SourceStringReader reader, Environment env) {
        int pos = reader.getPos();

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        List<T> list = new ArrayList<>();
        list.add(result.get());

        while(true) {
          pos = reader.getPos();
          result = parser.applyRule(reader, env);
          if (result.isFail()) break;
          else list.add(result.get());
        }

        reader.setPos(pos);
        return success(list);
      }
    };
  }

  public static <T> PackratParser<List<T>> rep (final PackratParser<T> parser, final PackratParser<?> sep) {
    return new PackratParser<List<T>>() {
      @Override
      protected ParseResult<List<T>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        List<T> list = new ArrayList<>();

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) {
          reader.setPos(pos);
          return success(list);
        }
        else list.add(result.get());

        while (true) {
          int sPos = reader.getPos();
          ParseResult<?> separator = sep.applyRule(reader, env);
          if (separator.isFail()) {
            reader.setPos(sPos);
            break;
          }

          result = parser.applyRule(reader, env);
          if (result.isFail()) return fail(result, pos, reader);
          else list.add(result.get());
        }

        return success(list);
      }
    };
  }

  public static <T> PackratParser<List<T>> rep1 (final PackratParser<T> parser, final PackratParser<?> sep) {
    return new PackratParser<List<T>>() {
      @Override
      protected ParseResult<List<T>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        List<T> list = new ArrayList<>();
        list.add(result.get());

        while (true) {
          final int sPos = reader.getPos();
          ParseResult<?> separator = sep.applyRule(reader, env);
          if (separator.isFail()) {
            reader.setPos(sPos);
            break;
          }

          result = parser.applyRule(reader, env);
          if (result.isFail()) return fail(result, pos, reader);
          else list.add(result.get());
        }

        return success(list);
      }
    };
  }

  @SafeVarargs
  public static <T> PackratParser<T> choice (final PackratParser<? extends  T>... parsers) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        List<ParseResult<?>> fails = new ArrayList<>();

        for (PackratParser<? extends T> parser : parsers) {
          ParseResult<? extends  T> result = parser.applyRule(reader, env, pos);
          if (result.isFail()) fails.add(result);
          else return success(result.get());
        }
        return fail(fails, pos, reader);
      }
    };
  }

  public static <T> PackratParser<T> choice (final List<PackratParser<? extends T>> parsers) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        List<ParseResult<?>> fails = new ArrayList<>();

        for (PackratParser<? extends T> parser : parsers) {
          ParseResult<? extends  T> result = parser.applyRule(reader, env, pos);
          if (result.isFail()) fails.add(result);
          else return success(result.get());
        }
        return fail(fails, pos, reader);
      }
    };
  }

  public static <S, T> PackratParser<T> foreach (final S[] c, final Function<S, PackratParser<T>> function, final String failMsg) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        for (S s : c) {
          ParseResult<T> result = function.apply(s).applyRule(reader, env, pos);
          if (! result.isFail()) return success(result.get());
        }
        return fail(failMsg, pos, reader);
      }
    };
  }

  public static <S, T, C extends Collection<S>> PackratParser<T> foreach (final C c, final Function<S, PackratParser<T>> function, final String failMsg) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();

        for (S s : c) {
          ParseResult<T> result = function.apply(s).applyRule(reader, env, pos);
          if (! result.isFail()) return success(result.get());
        }
        return fail(failMsg, pos, reader);
      }
    };
  }

  public static <T> PackratParser<T> depends (final Function<Environment, PackratParser<T>> env2parser) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        return env2parser.apply(env).applyRule(reader, env);
      }
    };
  }

  public static <T> PackratParser<T> ref (ParserThunk<T> reference) {
    return reference;
  }

  public static <T> PackratParser<T> scope (final PackratParser<T> parser) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        Environment newEnv = new Environment(env);
        ParseResult<T> result = parser.applyRule(reader, newEnv);
        if (!result.isFail()) env.inheritExceptions(newEnv);
        return result;
      }
    };
  }

  public static <T> PackratParser<Pair<T, Environment>> newEnvironment (final PackratParser<T> parser) {
    return new PackratParser<Pair<T, Environment>>() {
      @Override
      protected ParseResult<Pair<T, Environment>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        Environment newEnv = new Environment(env);
        ParseResult<T> result = parser.applyRule(reader, newEnv);
        if (!result.isFail()) return success(Pair.make(result.get(), newEnv));
        return fail(result, pos, reader);
      }
    };
  }

  public static <T, R> PackratParser<R> map (final PackratParser<T> parser, final Function<T, R> function) {
    return new PackratParser<R> () {
      @Override
      protected ParseResult<R> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);
        else return success(function.apply(result.get()));
      }
    };
  }

  public static <T, R> PackratParser<R> bind (final PackratParser<T> parser, final Function<T, PackratParser<R>> function) {
    return new PackratParser<R>() {
      @Override
      protected ParseResult<R> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T> resultT = parser.applyRule(reader, env);
        if (resultT.isFail()) return fail(resultT, pos, reader);
        ParseResult<R> resultR = function.apply(resultT.get()).applyRule(reader, env);
        if (resultR.isFail()) return fail(resultR, pos, reader);
        return resultR;
      }
    };
  }

  public static <T1, T2> PackratParser<Pair<T1, T2>> seq (final PackratParser<T1> p1, final PackratParser<T2> p2) {
    return new PackratParser<Pair<T1, T2>>() {
      @Override
      protected ParseResult<Pair<T1, T2>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T1> result1 = p1.applyRule(reader, env);
        if (result1.isFail()) return fail(result1, pos, reader);
        ParseResult<T2> result2 = p2.applyRule(reader, env);
        if (result2.isFail()) return fail(result2, pos, reader);
        return success(Pair.make(result1.get(), result2.get()));
      }
    };
  }

  public static <T1, T2, T3> PackratParser<Triad<T1, T2, T3>> seq (final PackratParser<T1> p1, final PackratParser<T2> p2, final PackratParser<T3> p3) {
    return new PackratParser<Triad<T1, T2, T3>>() {
      @Override
      protected ParseResult<Triad<T1, T2, T3>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T1> result1 = p1.applyRule(reader, env);
        if (result1.isFail()) return fail(result1, pos, reader);
        ParseResult<T2> result2 = p2.applyRule(reader, env);
        if (result2.isFail()) return fail(result2, pos, reader);
        ParseResult<T3> result3 = p3.applyRule(reader, env);
        if (result3.isFail()) return fail(result3, pos, reader);
        return success(Triad.make(result1.get(), result2.get(), result3.get()));
      }
    };
  }

  public static <T1, T2, T3, T4> PackratParser<Quad<T1, T2, T3, T4>> seq (final PackratParser<T1> p1, final PackratParser<T2> p2, final PackratParser<T3> p3, final PackratParser<T4> p4) {
    return new PackratParser<Quad<T1, T2, T3, T4>>() {
      @Override
      protected ParseResult<Quad<T1, T2, T3, T4>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T1> result1 = p1.applyRule(reader, env);
        if (result1.isFail()) return fail(result1, pos, reader);
        ParseResult<T2> result2 = p2.applyRule(reader, env);
        if (result2.isFail()) return fail(result2, pos, reader);
        ParseResult<T3> result3 = p3.applyRule(reader, env);
        if (result3.isFail()) return fail(result3, pos, reader);
        ParseResult<T4> result4 = p4.applyRule(reader, env);
        if (result4.isFail()) return fail(result4, pos, reader);
        return success(Quad.make(result1.get(), result2.get(), result3.get(), result4.get()));
      }
    };
  }

  public static <T> PackratParser<List<T>> sequence (final List<PackratParser<T>> parsers, final PackratParser<?> sep) {
    return new PackratParser<List<T>>() {
      @Override
      protected ParseResult<List<T>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        final int length = parsers.size();

        List<T> results = new ArrayList<>();

        if (length == 0) return success(results);

        ParseResult<T> result = parsers.get(0).applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);
        else results.add(result.get());

        for (int i = 1; i < length; i++) {
          ParseResult<?> separator = sep.applyRule(reader, env);
          if (separator.isFail()) return fail(separator, pos, reader);

          result = parsers.get(i).applyRule(reader, env);
          if (result.isFail()) return fail(result, pos, reader);
          else results.add(result.get());
        }

        return success(results);
      }
    };
  }

  public static <T> PackratParser<T> andPredicate (final PackratParser<T> predicate) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<T> result = predicate.applyRule(reader, env);
        reader.setPos(pos);
        return result;
      }
    };
  }

  public static PackratParser<ParseResult<?>> notPredicate (final PackratParser<?> predicate) {
    return new PackratParser<ParseResult<?>>() {
      @Override
      protected ParseResult<ParseResult<?>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<?> result = predicate.applyRule(reader, env);
        reader.setPos(pos);
        if (result.isFail()) return success(result);
        else return fail("success at not predicate", pos, reader);
      }
    };
  }

  public static <T> PackratParser<T> unit (final T t) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        return new Success<>(t);
      }
    };
  }

  public static <T> PackratParser<T> effect (final PackratParser<T> parser, final Effect eff) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        ParseResult<T> result = parser.applyRule(reader, env);
        if (! result.isFail()) eff.perform(reader, env);
        return result;
      }
    };
  }

  public static <T> PackratParser<T> withEffect (final PackratParser<T> parser, final Function<T, Effect> f) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        ParseResult<T> result = parser.applyRule(reader, env);
        if (! result.isFail()) f.apply(result.get()).perform(reader, env);
        return result;
      }
    };
  }

  public static <T> PackratParser<T> failure (final String msg) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        return new Failure<>(msg, reader.getPos(), reader.getLine());
      }
    };
  }

  public static <T> PackratParser<T> error (final NotFoundException e) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
        return new Failure<>(e.getMessage(), reader.getPos(), reader.getLine());
      }
    };
  }

  public static <T> PackratParser<T> error (final NotFoundError e) {
    return new PackratParser<T>() {
      @Override
      protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
        ErrorList.addError(new NotFoundError(e.getMessage(), reader.filePath, reader.getLine()));
        return new Failure<>(e.getMessage(), reader.getPos(), reader.getLine());
      }
    };
  }

  public static <S, T> PackratParser<Pair<List<S>, List<T>>> unzip (final PackratParser<List<Pair<S, T>>> parser) {
    return new PackratParser<Pair<List<S>, List<T>>>() {
      @Override
      protected ParseResult<Pair<List<S>, List<T>>> parse(SourceStringReader reader, Environment env) {
        final int pos = reader.getPos();
        ParseResult<List<Pair<S, T>>> result = parser.applyRule(reader, env);
        if (result.isFail()) return fail(result, pos, reader);

        List<S> ss = new ArrayList<>();
        List<T> ts = new ArrayList<>();

        for (Pair<S, T> pair : result.get()) {
          ss.add(pair._1);
          ts.add(pair._2);
        }

        return success(Pair.make(ss, ts));
      }
    };
  }

  public static Effect throwing (CtClass exceptionType) {
    return (reader, env) -> {
      try {
        env.addException(exceptionType, reader.getLine());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }
    };
  }

  public static Effect throwing (CtBehavior behavior) {
    return (reader, env) -> {
      try {
        env.addExceptions(behavior.getExceptionTypes(), reader.getLine());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }
    };
  }

  public static Effect throwing (IROperator operator) {
    if (operator.actualMethod != null) return (reader, env) -> {
      try {
        env.addExceptions(operator.actualMethod.getExceptionTypes(), reader.getLine());
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }
    };
    else return (reader, env) -> {};
  }

  public static Effect throwing (List<Environment> environments) {
    return (reader, env) -> {
      for (Environment e : environments) env.inheritExceptions(e);
    };
  }

  public static Effect catching (CtClass exceptionType) {
    return (reader, env) -> {
      try {
        if (! exceptionType.subtypeOf(CommonTypes.getInstance().throwableType)) {
          String msg = "No exception of type " + exceptionType.getName() + " can be thrown; an exception type must be a subclass of Throwable";
          ErrorList.addError(new SemanticsError(msg, reader.filePath, reader.getLine()));
        }
        else env.removeException(exceptionType);
      } catch (NotFoundException e) {
        ErrorList.addError(new NotFoundError(e, reader.filePath, reader.getLine()));
      }
    };
  }

  public static Effect declareLocal (String name, CtClass type) {
    return (reader, env) -> { env.declareLocal(name, type); };
  }

  public static abstract class ParserThunk<T> extends PackratParser<T> {
    public abstract PackratParser<T> evaluate();

    @Override
    protected ParseResult<T> parse(SourceStringReader reader, Environment env) {
      return evaluate().applyRule(reader, env);
    }
  }

  public interface Effect {
    public void perform (SourceStringReader reader, Environment env);
  }
}