package proteaj.pparser;

import proteaj.io.*;
import proteaj.ir.*;

import java.util.*;

/*
abstract class ComposedParser_Sequential<T> extends PackratParser<T> {
  protected ComposedParser_Sequential(String name) {
    this.name = name;
  }

  protected abstract PackratParser<? extends T>[] getParsers(Environment env);
  protected abstract TypedAST makeAST(int pos, int line, String file, TypedAST... as);

  @Override
  protected final ParseResult<T> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser<? extends T>[] ps = getParsers(env);
    List<ParseResult<T>> as = new ArrayList<ParseResult<T>>();
    for (int i = 0; i < ps.length; i++) {
      as[i] = ps[i].applyRule(reader, env);
      if (as[i].isFail()) {
        reader.setPos(pos);
        return as[i];
      }
    }
    TypedAST ret = makeAST(reader.getPos(), reader.getLine(), reader.getFilePath(), as);
    if (ret.isFail()) reader.setPos(pos);
    return ret;
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}*/

abstract class ComposedParser_Alternative<T> extends PackratParser<T> {
  protected ComposedParser_Alternative(String name) {
    this.name = name;
  }

  protected List<PackratParser<? extends  T>> asList(PackratParser<? extends  T>... parsers) {
    return Arrays.asList(parsers);
  }

  protected abstract List<PackratParser<? extends  T>> getParsers(Environment env);

  @Override
  protected final ParseResult<T> parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    List<ParseResult<?>> fails = new ArrayList<ParseResult<?>>();

    for (PackratParser<? extends  T> p : getParsers(env)) {
      ParseResult<? extends  T> ret = p.applyRule(reader, env, pos);
      if (! ret.isFail()) return success(ret.get());
      else fails.add(ret);
    }

    return fail(fails, pos, reader);
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}
/*
abstract class ComposedParser_Repetition extends PackratParser {
  protected ComposedParser_Repetition(String name) {
    this.name = name;
  }

  protected abstract PackratParser getParser(Environment env);
  protected abstract TypedAST makeAST(int pos, int line, String file, List<TypedAST> as);

  @Override
  protected TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser p = getParser(env);
    List<TypedAST> list = new ArrayList<TypedAST>();

    while(true) {
      TypedAST ast = p.applyRule(reader, env);
      if (ast.isFail()) break;

      list.add(ast);
      pos = reader.getPos();
    }

    reader.setPos(pos);
    return makeAST(pos, reader.getLine(), reader.getFilePath(), list);
  }

  @Override
  public String toString() {
    return name;
  }

  private final String name;
}
*/