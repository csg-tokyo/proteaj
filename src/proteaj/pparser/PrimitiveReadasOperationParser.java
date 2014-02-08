package proteaj.pparser;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.ir.tast.*;

import java.util.*;
import javassist.*;

import static proteaj.util.CtClassUtil.getConstructor;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isLetter;

public abstract class PrimitiveReadasOperationParser extends ReadasOperationParser {
  @Override
  protected abstract TypedAST parse(SourceStringReader reader, Environment env);

  public static PrimitiveReadasOperationParser getParser(CtClass type) {
    if(parsers.containsKey(type)) return parsers.get(type);
    return defaultParser;
  }

  public static void initAll() {
    if(parsers.isEmpty()) {
      addReadasIdentifierParser();
      addReadasLetterParser();
      addReadasTypeParser();
    }

    for(PrimitiveReadasOperationParser parser : parsers.values()) {
      parser.init();
    }
    defaultParser.init();
  }

  private static void addReadasIdentifierParser() {
    if(IRCommonTypes.getIdentifierType() != null) try {
      CtClass type = IRCommonTypes.getIdentifierType();
      CtConstructor constructor = getConstructor(type, IRCommonTypes.getStringType());
      PrimitiveReadasOperationParser parser = new ReadasIdentifierParser(type, constructor);
      parsers.put(type, parser);
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private static void addReadasLetterParser() {
    if(IRCommonTypes.getLetterType() != null) try {
      CtClass type = IRCommonTypes.getLetterType();
      CtConstructor constructor = getConstructor(type, CtClass.charType);
      PrimitiveReadasOperationParser parser = new ReadasLetterParser(type, constructor);
      parsers.put(type, parser);
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private static void addReadasTypeParser() {
    if(IRCommonTypes.getTypeType() != null) try {
      CtClass type = IRCommonTypes.getTypeType();
      CtConstructor constructor = getConstructor(type, IRCommonTypes.getClassType());
      PrimitiveReadasOperationParser parser = new ReadasTypeParser(type, constructor);
      parsers.put(type, parser);
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private PrimitiveReadasOperationParser(CtClass type) {
    super(type, IRDummyPattern.getDummy_Readas(), 0);
  }

  private static Map<CtClass, PrimitiveReadasOperationParser> parsers = new HashMap<CtClass, PrimitiveReadasOperationParser>();

  private static final PrimitiveReadasOperationParser defaultParser = new PrimitiveReadasOperationParser(CtClass.voidType) {
    @Override
    protected TypedAST parse(SourceStringReader reader, Environment env) {
      int pos = reader.getPos();
      int line = reader.getLine();

      StringBuilder buf = new StringBuilder();

      while(reader.hasNext()) {
        if(Character.isWhitespace(reader.lookahead())) break;
        buf.append(reader.next());
      }

      FailLog flog = new FailLog("fail to parse the readas operand : " + buf.toString(), pos, line);
      reader.setPos(pos);
      return new BadAST(flog);
    }
  };

  private static class ReadasIdentifierParser extends PrimitiveReadasOperationParser {
    public ReadasIdentifierParser(CtClass identifier, CtConstructor constructor) {
      super(identifier);
      this.constructor = constructor;
    }

    @Override
    protected TypedAST parse(SourceStringReader reader, Environment env) {
      int pos = reader.getPos();

      if(! isJavaIdentifierStart(reader.lookahead())) {
        FailLog flog = new FailLog("expected identifier, but found " + (char)reader.lookahead(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      StringBuilder buf = new StringBuilder();
      buf.append(reader.next());

      while(reader.hasNext() && isJavaIdentifierPart(reader.lookahead())) {
        buf.append(reader.next());
      }

      return new NewExpression(constructor, new Arguments(new StringLiteral(buf.toString())));
    }

    private CtConstructor constructor;
  }

  private static class ReadasLetterParser extends PrimitiveReadasOperationParser {
    public ReadasLetterParser(CtClass letter, CtConstructor constructor) {
      super(letter);
      this.constructor = constructor;
    }

    @Override
    protected TypedAST parse(SourceStringReader reader, Environment env) {
      int pos = reader.getPos();

      if(! isLetter(reader.lookahead())) {
        FailLog flog = new FailLog("expected letter, but found " + (char)reader.lookahead(), reader.getPos(), reader.getLine());
        reader.setPos(pos);
        return new BadAST(flog);
      }

      return new NewExpression(constructor, new Arguments(new CharLiteral(reader.next())));
    }

    private CtConstructor constructor;
  }

  private static class ReadasTypeParser extends PrimitiveReadasOperationParser {
    public ReadasTypeParser(CtClass type, CtConstructor constructor) {
      super(type);
      this.constructor = constructor;
    }


    @Override
    protected TypedAST parse(SourceStringReader reader, Environment env) {
      int pos = reader.getPos();

      TypedAST typename = TypeNameParser.parser.applyRule(reader, env);
      if(typename.isFail()) {
        reader.setPos(pos);
        return new BadAST(typename.getFailLog());
      }

      return new NewExpression(constructor, new Arguments(new ClassLiteral(((TypeName)typename).getType())));
    }

    private CtConstructor constructor;
  }
}

