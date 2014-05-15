package proteaj;

import proteaj.error.*;
import proteaj.token.*;
import proteaj.ast.*;
import proteaj.util.*;

import java.util.*;

public class SigParser {
  public SigParser(SigLexer lexer) throws ParseError {
    this.lexer = lexer;
    this.filePath = lexer.getFilePath();
    this.corresponds = createCorrespondsMap();

    lexer.init();
  }

  /* CompilationUnit
   *  : FileHeader FileBody
   */
  public CompilationUnit parseCompilationUnit() {
    FileHeader header = parseFileHeader();
    FileBody body = parseFileBody();

    return new CompilationUnit(filePath, header, body);
  }

  /* FileHeader
   *  : [ "package" QualifiedIdentifier ';' ]
   *    { "import" QualifiedIdentifier [ '.' '*' ] ';' }
   *    { "using" QualifiedIdentifier ';' }
   *    { "unusing" QualifiedIdentifier ';' }
   */
  public FileHeader parseFileHeader() {
    FileHeader header = new FileHeader(lexer.lookahead().getLine());

    // package declaration
    if(lexer.lookahead().is("package")) try {
      // "package"
      lexer.next();

      // QualifiedIdentifier
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid package declaration" +
            " : expected package name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      String pack = parseQualifiedIdentifier();

      // ';'
      if(! lexer.lookahead().is(';')) {
        throw new ParseError("invalid package declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }
      lexer.next();

      header.setPackageName(pack);
    } catch (ParseError e) {
      ErrorList.addError(e);
      lexer.nextLine();
    }

    // import declaration
    while(lexer.lookahead().is("import")) try {
      // "import"
      lexer.next();

      // QualifiedIdentifier
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid import declaration" +
            " : expected class/package name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      String imp = parseQualifiedIdentifier();

      // [ '.' '*' ] ';'
      if(lexer.lookahead().is('.') && lexer.lookahead(1).is('*') && lexer.lookahead(2).is(';')) {
        lexer.next(2);
        header.addImportPackage(imp);
      }
      else if(lexer.lookahead().is(';')) {
        lexer.next();
        header.addImportClass(imp);
      }
      else throw new ParseError("invalid import declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());

    } catch (ParseError e) {
      ErrorList.addError(e);
      lexer.nextLine();
    }

    // using declaration
    while(lexer.lookahead().is("using")) try {
      // "using"
      lexer.next();

      // QualifiedIdentifier
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid using declaration : expected operators name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      String ops = parseQualifiedIdentifier();

      // ';'
      if(! lexer.lookahead().is(';')) {
        throw new ParseError("invalid using declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }
      lexer.next();

      header.addUsingSyntax(ops);
    } catch (ParseError e) {
      ErrorList.addError(e);
      lexer.nextLine();
    }

    // unusing
    while(lexer.lookahead().is("unusing")) try {
      // "unusing"
      lexer.next();

      // QualifiedIdentifier
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid unusing declaration : expected operators name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      String ops = parseQualifiedIdentifier();

      // ';'
      if(! lexer.lookahead().is(';')) {
        throw new ParseError("invalid unusing declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }
      lexer.next();

      header.addUnusingSyntax(ops);
    } catch (ParseError e) {
      ErrorList.addError(e);
      lexer.nextLine();
    }

    return header;
  }

  /* FileBody
   *  : { Modifiers ( ClassDecl | InterfaceDecl | SyntaxDecl ) }
   */
  public FileBody parseFileBody() {
    FileBody body = new FileBody(lexer.lookahead().getLine());

    while(lexer.hasNext()) try {
      int mods = parseModifiers();

      if(lexer.lookahead().is("class")) {
        ClassDecl cdecl = parseClassDecl();
        cdecl.setModifiers(mods);
        body.addClass(cdecl);
      }
      else if(lexer.lookahead().is("interface")) {
        InterfaceDecl idecl = parseInterfaceDecl();
        idecl.setModifiers(mods);
        body.addInterface(idecl);
      }
      else if(lexer.lookahead().is("operators")) {
        SyntaxDecl syndecl = parseSyntaxDecl();
        syndecl.setModifiers(mods);
        body.addSyntax(syndecl);
      }
      else throw new ParseError("invalid class/interface/syntax declaration"
            + " : expected \"class\", \"interface\" or \"operators\", but found \""
            + lexer.lookahead().toString()
            + "\"", filePath, lexer.lookahead().getLine());
    } catch (ParseError e) {
      while(lexer.hasNext()) {
        if(lexer.lookahead().is("class") || lexer.lookahead().is("interface") || lexer.lookahead().is("operators")) break;
        else lexer.next();
      }
    }

    return body;
  }

  /* ClassDecl
   *  : "class" Identifier [ "extends" QualifiedIdentifier ]
   *    [ "implements" QualifiedIdentifier { ',' QualifiedIdentifier } ]
   *    '{' { StaticInitializer | Modifiers ( ConstructorDecl | MethodDecl | FieldDecl ) } '}'
   */
  public ClassDecl parseClassDecl() throws ParseError {
    // "class"
    assert lexer.lookahead().is("class");
    int line = lexer.next().getLine();

    // Identifier
    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid class declaration" +
          " : expected class name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    ClassDecl cdecl = new ClassDecl(lexer.next().toString(), line);

    // [ "extends" QualifiedIdentifier ]
    if(lexer.lookahead().is("extends")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid class declaration" +
            " : expected super class name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      cdecl.setSuperClass(parseQualifiedIdentifier());
    }

    // [ "implements" QualifiedIdentifier { ',' QualifiedIdentifier } ]
    if(lexer.lookahead().is("implements")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid class declaration" +
            " : expected interface name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }

      cdecl.addInterface(parseQualifiedIdentifier());

      while(lexer.lookahead().is(',') && lexer.lookahead(1).isIdentifier()) {
        lexer.next();
        cdecl.addInterface(parseQualifiedIdentifier());
      }
    }

    // '{'
    if(! lexer.lookahead().is('{')) {
      throw new ParseError("invalid class declaration : expected '{', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    Token lBrace = lexer.next();

    // { Modifiers ( ConstructorDecl | MethodDecl | FieldDecl ) }
    while(! lexer.lookahead().is('}')) try {
      if(lexer.lookahead().is("static") && lexer.lookahead(1).is('{')) {
        StaticInitializer sinit = parseStaticInitializer();
        cdecl.addStaticInitializer(sinit);
        continue;
      }

      // Modifiers
      int mods = parseModifiers();

      // ( ConstructorDecl | MethodDecl | FieldDecl )
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid class member : expected type name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }

      // Constructor
      if(lexer.lookahead(1).is('(')) {
        ConstructorDecl constructor = parseConstructorDecl();
        constructor.setModifiers(mods);
        cdecl.addConstructor(constructor);
        continue;
      }

      // ( MethodDecl | FieldDecl )
      int i = nextOfType(0);
      if(! lexer.lookahead(i).isIdentifier()) {
        throw new ParseError("invalid class member : expected member name, but found \"" + lexer.lookahead(i).toString() + "\"", filePath, lexer.lookahead(i).getLine());
      }
      i++;

      // MethodDecl
      if(lexer.lookahead(i).is('(')) {
        MethodDecl method = parseMethodDecl();
        method.setModifiers(mods);
        cdecl.addMethod(method);
      }
      // FieldDecl
      else {
        List<FieldDecl> fields = parseFieldsDecl();
        for (FieldDecl field : fields) {
          field.setModifiers(mods);
          cdecl.addField(field);
        }
      }
    } catch (ParseError e) {
      assert corresponds.containsKey(lBrace);
      ErrorList.addError(e);
      lexer.setPos(corresponds.get(lBrace));
    }

    assert lexer.lookahead().is('}');
    lexer.next();

    return cdecl;
  }

  /* StaticInitializer
   *  : "static" MethodBody
   */
  public StaticInitializer parseStaticInitializer() {
    int line = lexer.lookahead().getLine();

    assert lexer.lookahead().is("static") && lexer.lookahead(1).is('{');
    lexer.next();

    int lbody = lexer.lookahead().getLine();
    return new StaticInitializer(parseMethodBody(), lbody, line);
  }

  /* ConstructorDecl
   *  : Identifier Parameters ThrowsClause ( MethodBody | ';' )
   */
  public ConstructorDecl parseConstructorDecl() throws ParseError {
    int line = lexer.lookahead().getLine();

    // Identifier
    assert lexer.lookahead().isIdentifier();
    String name = lexer.next().toString();

    // Parameters
    assert lexer.lookahead().is('(');
    List<Parameter> params = parseParameters();

    // ThrowsClause
    List<String> exceptions = parseThrowsClause();

    ConstructorDecl constructor = new ConstructorDecl(name, params, exceptions, line);

    // ( MethodBody | ';' )
    if(lexer.lookahead().is('{')) {
      int lbody = lexer.lookahead().getLine();
      constructor.setBody(parseMethodBody(), lbody);
    }
    else if(lexer.lookahead().is(';')) {
      lexer.next();
    }
    else {
      throw new ParseError("invalid constructor declaration : expected '{' or ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }

    return constructor;
  }

  /* MethodDecl
   *  : Type Identifier Parameters ThrowsClause ( MethodBody | ';' )
   */
  public MethodDecl parseMethodDecl() throws ParseError {
    int line = lexer.lookahead().getLine();

    // Type
    assert lexer.lookahead().isIdentifier();
    String type = parseType();

    // Identifier
    assert lexer.lookahead().isIdentifier();
    String name = lexer.next().toString();

    // Parameters
    assert lexer.lookahead().is('(');
    List<Parameter> params = parseParameters();

    // ThrowsClause
    List<String> exceptions = parseThrowsClause();

    MethodDecl method = new MethodDecl(type, name, params, exceptions, line);

    // ( MethodBody | ';' )
    if(lexer.lookahead().is('{')) {
      int lbody = lexer.lookahead().getLine();
      method.setBody(parseMethodBody(), lbody);
    }
    else if(lexer.lookahead().is(';')) {
      lexer.next();
    }
    else {
      throw new ParseError("invalid method declaration : expected '{' or ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }

    return method;
  }

  /* FieldDecl
   *  : Type Identifier [ '=' Expression ] ';'
   */
/*  public FieldDecl parseFieldDecl() throws ParseError {
    int line = lexer.lookahead().getLine();

    // Type
    assert lexer.lookahead().isIdentifier();
    String type = parseType();

    // Identifier
    assert lexer.lookahead().isIdentifier();
    String name = lexer.next().toString();

    FieldDecl fdecl = new FieldDecl(type, name, line);

    // [ '=' Expression ]
    if(lexer.lookahead().is('=')) {
      int lbody = lexer.lookahead().getLine();
      fdecl.setBody(parseFieldBody(), lbody);
    }

    if(! lexer.lookahead().is(';')) {
      throw new ParseError("invalid field declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    lexer.next();

    return fdecl;
  }*/

  /* FieldsDecl
   *  : Type Identifier [ '=' Expression ] { ',' Identifier [ '=' Expression ] } ';'
   */
  public List<FieldDecl> parseFieldsDecl() throws ParseError {
    int line = lexer.lookahead().getLine();

    // Type
    assert lexer.lookahead().isIdentifier();
    String type = parseType();

    // Identifier
    assert lexer.lookahead().isIdentifier();
    String name = lexer.next().toString();

    FieldDecl fdecl = new FieldDecl(type, name, line);

    // [ '=' Expression ]
    if(lexer.lookahead().is('=')) {
      int lbody = lexer.lookahead().getLine();
      fdecl.setBody(parseFieldBody(), lbody);
    }

    List<FieldDecl> list = new ArrayList<>();
    list.add(fdecl);

    while(lexer.lookahead().is(',')) {
      lexer.next();

      // Identifier
      assert lexer.lookahead().isIdentifier();
      String id = lexer.next().toString();

      FieldDecl field = new FieldDecl(type, id, line);

      // [ '=' Expression ]
      if(lexer.lookahead().is('=')) {
        int lbody = lexer.lookahead().getLine();
        field.setBody(parseFieldBody(), lbody);
      }

      list.add(field);
    }

    if(! lexer.lookahead().is(';')) {
      throw new ParseError("invalid field declaration : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    lexer.next();

    return list;
  }

  /* InterfaceDecl
   *  : "interface" Identifier [ "extends" QualifiedIdentifier { ',' QualifiedIdentifier } ]
   *    '{' { Modifiers ( FieldDecl | MethodDecl ) } '}'
   */
  public InterfaceDecl parseInterfaceDecl() throws ParseError {
    // "interface"
    assert lexer.lookahead().is("interface");
    int line = lexer.next().getLine();

    // Identifier
    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid interface declaration : expected interface name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    InterfaceDecl idecl = new InterfaceDecl(lexer.next().toString(), line);

    // [ "extends" QualifiedIdentifier { ',' QualifiedIdentifier } ]
    if(lexer.lookahead().is("extends")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid interface declaration" +
            " : expected interface name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }

      idecl.addInterface(parseQualifiedIdentifier());

      while(lexer.lookahead().is(',') && lexer.lookahead(1).isIdentifier()) {
        lexer.next();
        idecl.addInterface(parseQualifiedIdentifier());
      }
    }

    // '{'
    if(! lexer.lookahead().is('{')) {
      throw new ParseError("invalid interface declaration : expected '{', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    Token lBrace = lexer.next();

    // { Modifiers ( MethodDecl | FieldDecl ) }
    while(! lexer.lookahead().is('}')) try {
      // Modifiers
      int mods = parseModifiers();

      // ( MethodDecl | FieldDecl )
      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid interface member : expected type name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }

      int i = nextOfType(0);
      if(! lexer.lookahead(i).isIdentifier()) {
        throw new ParseError("invalid interface member : expected member name, but found \"" + lexer.lookahead(i).toString() + "\"", filePath, lexer.lookahead(i).getLine());
      }
      i++;

      // MethodDecl
      if(lexer.lookahead(i).is('(')) {
        MethodDecl method = parseMethodDecl();
        method.setModifiers(mods);
        idecl.addMethod(method);
      }
      // FieldDecl
      else {
        List<FieldDecl> fields = parseFieldsDecl();
        for (FieldDecl field : fields) {
          field.setModifiers(mods);
          idecl.addField(field);
        }
      }
    } catch (ParseError e) {
      assert corresponds.containsKey(lBrace);
      ErrorList.addError(e);
      lexer.setPos(corresponds.get(lBrace));
    }

    assert lexer.lookahead().is('}');
    lexer.next();

    return idecl;
  }

  /* SyntaxDecl
   *  : "operators" Identifier [ "extends" QualifiedIdentifier ] [ "mixin" QualifiedIdentifier { ',' QualifiedIdentifier } ]
   *    '{' { [ Identifier ':' ] Modifiers OperatorDecl } '}'
   */
  public SyntaxDecl parseSyntaxDecl() throws ParseError {
    // "operators"
    assert lexer.lookahead().is("operators");
    int line = lexer.next().getLine();

    // Identifier
    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid operators declaration : expected operators name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    SyntaxDecl syndecl = new SyntaxDecl(lexer.next().toString(), line);

    // [ "extends" QualifiedIdentifier ]
    if(lexer.lookahead().is("extends")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid operators declaration" +
            " : expected base operators name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }
      syndecl.setBaseOperators(parseQualifiedIdentifier());
    }

    // under construction
    // [ "mixin" QualifiedIdentifier { ',' QualifiedIdentifier } ]
    if(lexer.lookahead().is("mixin")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid operators declaration" +
            " : expected operators name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
      }

      syndecl.addMixinOperators(parseQualifiedIdentifier());

      while(lexer.lookahead().is(',') && lexer.lookahead(1).isIdentifier()) {
        lexer.next();
        syndecl.addMixinOperators(parseQualifiedIdentifier());
      }
    }

    // '{'
    if(! lexer.lookahead().is('{')) {
      throw new ParseError("invalid operators declaration : expected '{', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    Token lBrace = lexer.next();

    // { [ Identifier ':' ] Modifiers OperatorDecl }
    while(! lexer.lookahead().is('}')) try {
      String name = null;
      if(lexer.lookahead().isIdentifier() && lexer.lookahead(1).is(':')) {
        name = lexer.next().toString();
        lexer.next();
      }

      int mods = parseModifiers();
      OperatorDecl operator = parseOperatorDecl();
      operator.setModifiers(mods);
      if(name != null) operator.setName(name);
      syndecl.addOperator(operator);
    } catch (ParseError e) {
      assert corresponds.containsKey(lBrace);
      ErrorList.addError(e);
      lexer.setPos(corresponds.get(lBrace));
    }

    // '}'
    assert lexer.lookahead().is('}');
    lexer.next();

    return syndecl;
  }

  /* OperatorDecl
   *  : TypeParameters Type OperatorPattern Parameters ThrowsClause [ ':' "priority" '=' IntValue ] ( MethodBody | ';' )
   */
  public OperatorDecl parseOperatorDecl() throws ParseError {
    int line = lexer.lookahead().getLine();

    // TypeParameters
    List<TypeParameter> typeParams = parseTypeParameters();

    // Type
    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid operator declaration : expected type name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    String type = parseType();

    // OperatorPattern
    OperatorPattern pattern = parseOperatorPattern();

    // Parameters
    if(! lexer.lookahead().is('(')) {
      throw new ParseError("invalid operator declaration : expected '(', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }
    List<Parameter> params = parseParameters();

    // ThrowsClause
    List<String> exceptions = parseThrowsClause();

    // [ ':' "priority" '=' IntLiteral ]
    int priority = 0;
    if(lexer.lookahead().is(':')
        && lexer.lookahead(1).is("priority")
        && lexer.lookahead(2).is('=')
        && lexer.lookahead(3).isIntLiteral()) {
      priority = Integer.parseInt(lexer.next(3).toString());
    }

    OperatorDecl operator = new OperatorDecl(typeParams, type, pattern, params, priority, exceptions, line);

    // ( MethodBody | ';' )
    if(lexer.lookahead().is('{')) {
      int bline = lexer.lookahead().getLine();
      operator.setBody(parseMethodBody(), bline);
    }
    else if(lexer.lookahead().is(';')) {
      lexer.next();
    }
    else {
      throw new ParseError("invalid operator declaration : expected '{' or ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    }

    return operator;
  }

  /* OperatorPattern
   *  : { Operand | Operator | AndPredicate | NotPredicate }+
   */
  public OperatorPattern parseOperatorPattern() throws ParseError {
    int line = lexer.lookahead().getLine();

    OperatorPattern pattern = new OperatorPattern(line);

    while(true) {
      if(lexer.lookahead().isIdentifier()) pattern.append(parseOperand());
      else if(lexer.lookahead().isStringLiteral()) pattern.append(parseOperator());
      else if(lexer.lookahead().is('&')) pattern.append(parseAndPredicate());
      else if(lexer.lookahead().is('!')) pattern.append(parseNotPredicate());
      else break;
    }

    return pattern;
  }

  /* Operand
   *  : Identifier [ ( '*' | '+' ) [ '(' StringLiteral ')' ] | '?' ]
   */
  public Operand parseOperand() {
    int line = lexer.lookahead().getLine();

    // Identifier
    assert lexer.lookahead().isIdentifier();
    String name = lexer.next().toString();

    Operand operand = new Operand(name, line);

    if(lexer.lookahead().is('*') || lexer.lookahead().is('+')) {
      if(lexer.lookahead().is('*')) {
        lexer.next();
        operand.setStarOption();
      }
      else {
        lexer.next();
        operand.setPlusOption();
      }

      if(lexer.lookahead().is('(') && lexer.lookahead(1).isStringLiteral() && lexer.lookahead(2).is(')')) {
        lexer.next();
        StringLiteral arg = (StringLiteral) lexer.next();
        operand.setOptionArg(arg.getValue());
        lexer.next();
      }
    }
    else if(lexer.lookahead().is('?')) {
      lexer.next();
      operand.setQuestionOption();
    }

    return operand;
  }

  /* Operator
   *  : StringLiteral
   */
  public Operator parseOperator() {
    int line = lexer.lookahead().getLine();

    assert lexer.lookahead().isStringLiteral();
    StringLiteral keyword = (StringLiteral) lexer.next();

    return new Operator(keyword.getValue(), line);
  }

  /* AndPredicate
   *  : '&' Type
   */
  public AndPredicate parseAndPredicate() throws ParseError {
    int line = lexer.lookahead().getLine();

    assert lexer.lookahead().is('&');
    lexer.next();

    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid and predicate : expected type name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    String type = parseType();

    return new AndPredicate(type, line);
  }

  /* NotPredicate
   *  : '!' Type
   */
  public NotPredicate parseNotPredicate() throws ParseError {
    int line = lexer.lookahead().getLine();

    assert lexer.lookahead().is('!');
    lexer.next();

    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid not predicate : expected type name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    String type = parseType();

    return new NotPredicate(type, line);
  }

  /* Modifiers
   *  : { Modifier }
   */
  private int parseModifiers() {
    int mods = 0;

    while(lexer.lookahead().isIdentifier()) {
      String id = lexer.lookahead().toString();
      if(! Modifiers.isModifier(id)) break;

      mods |= Modifiers.getModifiersMask(id);
      lexer.next();
    }

    return mods;
  }

  /* TypeParameters
   *  : [ '<' TypeParameter { ',' TypeParameter } '>' ]
   */
  private List<TypeParameter> parseTypeParameters() throws ParseError {
    List<TypeParameter> params = new ArrayList<>();

    if (! lexer.lookahead().is('<')) return params;
    lexer.next();

    params.add(parseTypeParameter());

    while (lexer.lookahead().is(',')) {
      lexer.next();
      params.add(parseTypeParameter());
    }

    if (! lexer.lookahead().is('>')) throw new ParseError("invalid type parameters : expected '>', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
    lexer.next();

    return params;
  }

  /* TypeParameter
   *  : Identifier [ "extends" Type ]
   */
  private TypeParameter parseTypeParameter() throws ParseError {
    if (! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid type parameter : expected type name, but found " + lexer.lookahead().toString(), filePath, lexer.lookahead().getLine());
    }
    Token id = lexer.next();

    if (lexer.lookahead().is("extends")) {
      lexer.next();
      return new TypeParameter(id.toString(), parseType(), id.getLine());
    }
    else return new TypeParameter(id.toString(), id.getLine());
  }

  /* Parameters
   *  : '(' [ Parameter { ',' Parameter } ] ')'
   */
  private List<Parameter> parseParameters() throws ParseError {
    List<Parameter> params = new ArrayList<>();

    assert lexer.lookahead().is('(');
    Token lParen = lexer.next();

    while(! lexer.lookahead().is(')')) try {
      params.add(parseParameter());

      if(lexer.lookahead().is(',')) {
        lexer.next();
        continue;
      }

      if(! lexer.lookahead().is(')')) {
        throw new ParseError("invald parameters : expected ')', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }

    } catch (ParseError e) {
      assert corresponds.containsKey(lParen);
      ErrorList.addError(e);
      lexer.setPos(corresponds.get(lParen));
    }

    assert lexer.lookahead().is(')');
    lexer.next();

    return params;
  }

  /* Parameter
   *  : Modifiers Type [ "..." ] Identifier [ '=' Expression ]
   */
  private Parameter parseParameter() throws ParseError {
    int line = lexer.lookahead().getLine();
    int mods = parseModifiers();

    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid parameter : expected parameter type, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    String type = parseType();

    if(lexer.lookahead().is('.') && lexer.lookahead(1).is('.') && lexer.lookahead(2).is('.')) {
      lexer.next(2);
      mods |= Modifiers.VARARGS;
      type += "[]";
    }

    if(! lexer.lookahead().isIdentifier()) {
      throw new ParseError("invalid parameter : expected parameter name, but found \"" + lexer.lookahead().toString() + "\"", filePath, lexer.lookahead().getLine());
    }
    String name = lexer.next().toString();

    Parameter param = new Parameter(type, name, line);
    param.setModifiers(mods);

    if(lexer.lookahead().is('=')) {
      int dvline = lexer.lookahead().getLine();
      param.setDefaultValue(parseDefaultValue(), dvline);
    }

    return param;
  }

  /* ThrowsClause
   *  : [ "throws" QualifiedIdentifier { ',' QualifiedIdentifier } ]
   */
  private List<String> parseThrowsClause() throws ParseError {
    List<String> exceptions = new ArrayList<>();

    if(lexer.lookahead().is("throws")) {
      lexer.next();

      if(! lexer.lookahead().isIdentifier()) {
        throw new ParseError("invalid throws clause : expected exception type name, but found " + lexer.lookahead().toString(), filePath, lexer.lookahead().getLine());
      }

      exceptions.add(parseQualifiedIdentifier());

      while(lexer.lookahead().is(',')) {
        lexer.next();

        if(! lexer.lookahead().isIdentifier()) {
          throw new ParseError("invalid throws clause : expected exception type name, but found " + lexer.lookahead().toString(), filePath, lexer.lookahead().getLine());
        }

        exceptions.add(parseQualifiedIdentifier());
      }
    }

    return exceptions;
  }

  /* Type
   *  : QualifiedIdentifier { '[' ']' }
   */
  private String parseType() {
    StringBuilder buf = new StringBuilder();
    buf.append(parseQualifiedIdentifier());

    while(lexer.lookahead().is('[') && lexer.lookahead(1).is(']')) {
      lexer.next(1);
      buf.append("[]");
    }

    return buf.toString();
  }

  /* QualifiedIdentifier
   *  : Identifier { '.' Identifier }
   */
  private String parseQualifiedIdentifier() {
    assert lexer.lookahead().isIdentifier();

    StringBuilder buf = new StringBuilder();
    buf.append(lexer.next().toString());

    while(lexer.lookahead().is('.') && lexer.lookahead(1).isIdentifier()) {
      buf.append(lexer.next()).append(lexer.next());
    }

    return buf.toString();
  }

  /* MethodBody
   *  : '{' ... '}'
   */
  private String parseMethodBody() {
    assert lexer.lookahead().is('{');

    Token lBrace = lexer.lookahead();
    assert corresponds.containsKey(lBrace);

    int begin = lexer.getPos();
    int end = corresponds.get(lBrace);

    lexer.setPos(end);
    assert lexer.lookahead().is('}');
    lexer.next();

    return lexer.toString(begin, end);
  }

  /* FieldBody
   *  : '=' Expression
   */
  private String parseFieldBody() throws ParseError {
    assert lexer.lookahead().is('=');

    lexer.next();

    int beg = lexer.getPos();
    int end = beg;

    while(! lexer.lookahead().is(';')) {
      if(! lexer.hasNext()) {
        throw new ParseError("invalid field initializer : expected ';', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }

      end = lexer.getPos();
      lexer.next();
    }

    return lexer.toString(beg, end);
  }

  /* DefaultValue
   *  : '=' Expression
   */
  private String parseDefaultValue() throws ParseError {
    assert lexer.lookahead().is('=');

    lexer.next();

    int beg = lexer.getPos();
    int end = beg;

    while(! (lexer.lookahead().is(',') || lexer.lookahead().is(')'))) {
      if(! lexer.hasNext()) {
        throw new ParseError("invalid parameters : expected ')', but found '" + lexer.lookahead().toString() + "'", filePath, lexer.lookahead().getLine());
      }

      end = lexer.getPos();
      lexer.next();
    }

    return lexer.toString(beg, end);
  }

  private int nextOfType(int i) {
    assert lexer.lookahead(i).isIdentifier();
    i++;

    while(lexer.lookahead(i).is('.') && lexer.lookahead(i + 1).isIdentifier()) i += 2;
    while(lexer.lookahead(i).is('[') && lexer.lookahead(i + 1).is(']')) i += 2;

    return i;
  }

  private Map<Token, Integer> createCorrespondsMap() throws ParseError {
    lexer.init();
    Map<Token, Integer> corresponds = new HashMap<>();
    Deque<Token> stack = new ArrayDeque<>();

    while(lexer.hasNext()) {
      Token token = lexer.lookahead();

      if(token.is('(') || token.is('{') || token.is('[')) {
        stack.push(token);
      }
      else if(token.is(')') || token.is('}') || token.is(']')) {
        char left = getCorrespondingChar(token);
        if(stack.isEmpty()) throw new ParseError("invalid token : uncorresponding '" + token.toString() + "'", filePath, lexer.lookahead().getLine());

        Token lparen = stack.peek();
        if(! lparen.is(left))
          throw new ParseError("invalid token : expected '" + lparen.toString() + "', but found '" + token.toString() + "'", filePath, lexer.lookahead().getLine());

        corresponds.put(lparen, lexer.getPos());
        stack.pop();
      }
      lexer.next();
    }

    if (! stack.isEmpty()) {
      throw new ParseError("invalid token : uncorresponding '" + stack.peek().toString() + "'", filePath, stack.peek().getLine());
    }

    return corresponds;
  }

  private char getCorrespondingChar(Token token) {
    assert token.is('(') || token.is('{') || token.is('[')
        || token.is(')') || token.is('}') || token.is(']');

    if(token.is('(')) return ')';
    else if(token.is(')')) return '(';
    else if(token.is('{')) return '}';
    else if(token.is('}')) return '{';
    else if(token.is('[')) return ']';
    else if(token.is(']')) return '[';
    else return 0;
  }

  private String filePath;
  private SigLexer lexer;

  private Map<Token, Integer> corresponds;
}

