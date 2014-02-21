package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;

abstract class ComposedParser_Sequential extends PackratParser {
  protected ComposedParser_Sequential(String name) {
    this.name = name;
  }

  protected abstract PackratParser[] getParsers(Environment env);
  protected abstract TypedAST makeAST(int pos, int line, String file, TypedAST... as);

  @Override
  protected final TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser[] ps = getParsers(env);
    TypedAST[] as = new TypedAST[ps.length];
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
}

abstract class ComposedParser_Alternative extends PackratParser {
  protected ComposedParser_Alternative(String name) {
    this.name = name;
  }

  protected abstract PackratParser[] getParsers(Environment env);

  @Override
  protected final TypedAST parse(SourceStringReader reader, Environment env) {
    int pos = reader.getPos();
    PackratParser[] ps = getParsers(env);
    FailLog flog = new FailLog("Suitable parser is not found", pos, reader.getLine());

    for (PackratParser p : ps) {
      TypedAST ret = p.applyRule(reader, env, pos);
      if (! ret.isFail()) return ret;
      else if (flog == null || flog.getEndPosition() < ret.getFailLog().getEndPosition()) flog = ret.getFailLog();
    }

    reader.setPos(pos);
    return new BadAST(flog);
  }

  @Override
  public String toString() {
    return name;
  }

  private String name;
}

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
