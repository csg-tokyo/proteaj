package proteaj;

import proteaj.error.*;
import proteaj.ast.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.env.type.*;
import proteaj.util.*;

import java.util.*;
import java.util.stream.IntStream;

import javassist.*;

import static proteaj.util.Modifiers.*;

public class SigIRGenerator {
  public SigIRGenerator(Collection<CompilationUnit> cunits) {
    this.cunits = cunits;
    this.root = RootTypeResolver.getInstance();
  }

  public IR generateIR() {
    IR ir = new IR();

    Collection<Pair<IRHeader, FileBody>> files = analyzeHeaders(cunits);
    Map<IRClass, ClassDecl> classes = registerClasses(files, ir);
    Map<IRClass, InterfaceDecl> interfaces = registerInterfaces(files, ir);
    Map<IRClass, SyntaxDecl> syntax = registerSyntax(files, ir);

    loadPrimitiveOperators(ir);

    classes.forEach((clazz, cdecl) -> {
      registerSuperClasses       (clazz, cdecl);
      registerStaticInitializers (clazz, cdecl, ir);
      registerConstructors       (clazz, cdecl, ir);
      registerMethods            (clazz, cdecl, ir);
      registerFields             (clazz, cdecl, ir);
    });

    interfaces.forEach((clazz, idecl) -> {
      registerSuperInterfaces    (clazz, idecl);
      registerInterfaceMethods   (clazz, idecl);
      registerInterfaceFields(clazz, idecl, ir);
    });

    syntax.forEach((clazz, sdecl) -> registerOperatorModule(clazz, sdecl, ir));

    return ir;
  }

  private Collection<Pair<IRHeader, FileBody>> analyzeHeaders(Collection<CompilationUnit> cunits) {
    Collection<Pair<IRHeader, FileBody>> files = new ArrayList<>();

    for(CompilationUnit cunit : cunits) {
      FileHeader header = cunit.header;
      FileBody body = cunit.body;

      IRHeader hdata = new IRHeader(cunit.filePath, header.getPackName(), header.getImportPackages(),
          header.getImportClasses(), header.getUsingSyntax(), header.getUnusingSyntax());

      files.add(Pair.make(hdata, body));
    }

    return files;
  }

  private IRClass makeClass (int mod, String name, IRHeader header) {
    CtClass ctClass = root.makeClass(mod, appendPackageName(header.packageName, name));
    return new IRClass(ctClass, header);
  }

  private IRClass makeInterface (int mod, String name, IRHeader header) {
    CtClass ctClass = root.makeInterface(mod, appendPackageName(header.packageName, name));
    return new IRClass(ctClass, header);
  }

  private Map<IRClass, ClassDecl> registerClasses(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Map<IRClass, ClassDecl> classes = new HashMap<>();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader header = pair._1;
      FileBody body = pair._2;

      for(ClassDecl cdecl : body.getClasses()) {
        IRClass clazz = makeClass(cdecl.getModifiers(), cdecl.getName(), header);
        ir.addClass(clazz);
        classes.put(clazz, cdecl);
      }
    }

    return classes;
  }

  private Map<IRClass, InterfaceDecl> registerInterfaces(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Map<IRClass, InterfaceDecl> ifaces = new HashMap<>();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader hdata = pair._1;
      FileBody body = pair._2;

      for(InterfaceDecl idecl : body.getInterfaces()) {
        IRClass clazz = makeInterface(idecl.getModifiers(), idecl.getName(), hdata);
        ir.addClass(clazz);
        ifaces.put(clazz, idecl);
      }
    }

    return ifaces;
  }

  private Map<IRClass, SyntaxDecl> registerSyntax(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Map<IRClass, SyntaxDecl> syntax = new HashMap<>();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader header = pair._1;

      for(String ops : header.usingSyntax) try {  // TODO :  if (! compilations.contains(ops))
        loadOperatorsFile(ops, ir);
      } catch (FileIOError e) {
        ErrorList.addError(e);
      }
    }

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader hdata = pair._1;
      FileBody body = pair._2;

      for(SyntaxDecl syn : body.getSyntax()) {
        IRClass clazz = makeClass(syn.getModifiers(), syn.getName(), hdata);
        ir.addClass(clazz);
        syntax.put(clazz, syn);
      }
    }

    return syntax;
  }

  private void loadPrimitiveOperators (IR ir) {
    ir.getOperatorPool().loadPrimitiveOperators();
  }

  private void registerSuperClasses(IRClass clazz, ClassDecl cdecl) {
    TypeResolver resolver = clazz.resolver;
    try {
      clazz.setSuperclass(resolver.getType(cdecl.getSuperClass()));
      for(String iface : cdecl.getInterfaces()) clazz.addInterface(resolver.getType(iface));
    } catch (NotFoundError | SemanticsError e) {
      ErrorList.addError(e.at(cdecl.line));
    }
  }

  private void registerStaticInitializers (IRClass clazz, ClassDecl cdecl, IR ir) {
    cdecl.getStaticInitializers().forEach(sInit -> registerStaticInitializer(clazz, sInit, ir));
  }

  private void registerStaticInitializer (IRClass clazz, StaticInitializer sInit, IR ir) {
    try {
      IRStaticInitializer irStaticInitializer = clazz.makeStaticInitializer(sInit.getBody(), sInit.getBodyLine());
      ir.addStaticInitializer(irStaticInitializer);
    } catch (SemanticsError e) {
      ErrorList.addError(e.at(sInit.line));
    }
  }

  private void registerConstructors (IRClass clazz, ClassDecl cdecl, IR ir) {
    if (cdecl.getConstructors().isEmpty()) clazz.addDefaultConstructor();
    else cdecl.getConstructors().forEach(constructor -> registerConstructor(clazz, constructor, ir));
  }

  private void registerConstructor (IRClass clazz, ConstructorDecl constructor, IR ir) {
    TypeResolver resolver = clazz.resolver;

    int mods = constructor.getModifiers();
    if (lastParamIsVarArgs(constructor.params)) mods |= Modifiers.VARARGS;

    try {
      CtClass[] paramTypes = getParamTypes(constructor.params, resolver);
      CtClass[] exceptions = getTypes(constructor.exceptions, resolver);

      if (constructor.hasBody()) {
        IRConstructor irConstructor = clazz.makeConstructor(mods, paramTypes, getParamNames(constructor.params), exceptions, constructor.getBody(), constructor.getBodyLine());
        ir.addConstructor(irConstructor);
      } else {
        clazz.addAbstractConstructor(mods, paramTypes, exceptions);
      }
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(constructor.line)); }
  }

  private void registerMethods (IRClass clazz, ClassDecl cdecl, IR ir) {
    cdecl.getMethods().forEach(method -> registerMethod(clazz, method, ir));
  }

  private void registerMethod (IRClass clazz, MethodDecl method, IR ir) {
    TypeResolver resolver = clazz.resolver;

    int mods = method.getModifiers();
    if(lastParamIsVarArgs(method.params)) mods |= Modifiers.VARARGS;

    try {
      CtClass returnType = resolver.getType(method.returnType);
      CtClass[] paramTypes = getParamTypes(method.params, resolver);
      CtClass[] exceptions = getTypes(method.exceptions, resolver);

      if (method.hasBody()) {
        IRMethod irMethod = clazz.makeMethod(mods, returnType, method.name, paramTypes, getParamNames(method.params), exceptions, method.getBody(), method.getBodyLine());
        ir.addMethod(irMethod);
      } else {
        clazz.addAbstractMethod(mods, returnType, method.name, paramTypes, exceptions);
      }
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(method.line)); }
  }

  private void registerFields (IRClass clazz, ClassDecl cdecl, IR ir) {
    cdecl.getFields().forEach(field -> registerField(clazz, field, ir));
  }

  private void registerField (IRClass clazz, FieldDecl field, IR ir) {
    try {
      CtClass type = clazz.resolver.getType(field.type);

      if (field.hasBody()) {
        IRField irField = clazz.makeField(field.getModifiers(), type, field.name, field.getBody(), field.getBodyLine());
        ir.addField(irField);
      } else {
        clazz.addAbstractField(field.getModifiers(), type, field.name);
      }
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(field.line)); }
  }

  private void registerSuperInterfaces (IRClass clazz, InterfaceDecl idecl) {
    TypeResolver resolver = clazz.resolver;
    for (String iface : idecl.getInterfaces()) try {
      clazz.addInterface(resolver.getType(iface));
    } catch (NotFoundError e) {
      ErrorList.addError(e.at(idecl.line));
    }
  }

  private void registerInterfaceMethods (IRClass clazz, InterfaceDecl idecl) {
    idecl.getMethods().forEach(method -> registerInterfaceMethod(clazz, method));
  }

  private void registerInterfaceMethod (IRClass clazz, MethodDecl method) {
    TypeResolver resolver = clazz.resolver;

    int mods = method.getModifiers() | Modifiers.PUBLIC | Modifiers.ABSTRACT;
    if(lastParamIsVarArgs(method.params)) mods |= Modifiers.VARARGS;

    try {
      CtClass returnType = resolver.getType(method.returnType);
      CtClass[] paramTypes = getParamTypes(method.params, resolver);
      CtClass[] exceptions = getTypes(method.exceptions, resolver);

      clazz.addAbstractMethod(mods, returnType, method.name, paramTypes, exceptions);
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(method.line)); }
  }

  private void registerInterfaceFields (IRClass clazz, InterfaceDecl idecl, IR ir) {
    idecl.getFields().forEach(field -> registerInterfaceField(clazz, field, ir));
  }

  private void registerInterfaceField (IRClass clazz, FieldDecl field, IR ir) {
    int mods = field.getModifiers() | Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
    try {
      CtClass type = clazz.resolver.getType(field.type);

      assert field.hasBody();
      IRField irField = clazz.makeField(mods, type, field.name, field.getBody(), field.getBodyLine());
      ir.addField(irField);
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(field.line)); }
  }

  private void registerOperatorModule (IRClass clazz, SyntaxDecl sdecl, IR ir) {
    IRSyntax irSyntax = new IRSyntax(clazz.clazz);

    if (sdecl.hasBaseOperators()) try {
      String base = sdecl.getBaseOperators();
      loadOperatorsFile(base, ir);
      irSyntax.setBaseSyntax(base);
    } catch (FileIOError e) { ErrorList.addError(e.at(sdecl.line)); }

    registerOperators(clazz, irSyntax, sdecl, ir);

    ir.addSyntax(irSyntax);
  }

  private void registerOperators (IRClass clazz, IRSyntax irSyntax, SyntaxDecl sdecl, IR ir) {
    sdecl.getOperators().forEach(operator -> registerOperator(clazz, irSyntax, operator, ir));
  }

  private void registerOperator (IRClass clazz, IRSyntax irSyntax, OperatorDecl odecl, IR ir) {
    TypeResolver resolver = clazz.resolver;

    int mods = odecl.getModifiers() | Modifiers.STATIC;
    String name = odecl.hasName() ? odecl.getName() : clazz.makeUniqueName("$ProteaJAutoGeneratedMethod");
    IROperandAttribute[] paramMods = getParamModifiers(odecl.pattern, odecl.params);

    try {
      CtClass returnType = resolver.getType(odecl.type);
      CtClass[] paramTypes = getParamTypes(odecl.params, resolver);
      CtClass[] andPreds = getAndPredicateTypes(odecl.pattern, resolver);
      CtClass[] notPreds = getNotPredicateTypes(odecl.pattern, resolver);
      CtClass[] bounds = getTypes(odecl.bounds, resolver);
      CtClass[] exceptions = getTypes(odecl.exceptions, resolver);

      registerDefaultArguments(clazz, paramMods, paramTypes, odecl.params, ir);

      IRMethod method = clazz.makeMethod(mods, returnType, name, paramTypes, getParamNames(odecl.params), exceptions, odecl.getBody(), odecl.getBodyLine());
      ir.addMethod(method);

      IROperator operator = new IROperator(mods, returnType, bounds, odecl.pattern, paramTypes, paramMods, andPreds, notPreds, odecl.priority, method.ctMethod);
      irSyntax.addOperator(operator);
    } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(odecl.line)); }
  }

  private void registerDefaultArguments (IRClass clazz, IROperandAttribute[] attrs, CtClass[] types, List<Parameter> params, IR ir) {
    IntStream.range(0, attrs.length).filter(i -> attrs[i].isOption()).forEach(i -> {
      String name = clazz.makeUniqueName("$ProteaJAutoGeneratedMethod");
      Parameter param = params.get(i);
      try {
        CtMethod method = clazz.makeCtMethod(Modifiers.PUBLIC | Modifiers.STATIC, types[i], name, new CtClass[0], new CtClass[0]);
        attrs[i].setDefaultMethod(method);
        ir.addDefaultArgument(new IRDefaultArgument(method, param.getDefaultValue(), param.getDefaultValueLine()));
      } catch (NotFoundError | SemanticsError e) { ErrorList.addError(e.at(param.getDefaultValueLine())); }
    });
  }

  private boolean loadOperatorsFile(String name, IR ir) throws FileIOError {
    OperatorPool opool = ir.getOperatorPool();

    boolean ret = true;

    if(! opool.containsSyntax(name)) {
      OperatorsFile ofile = OperatorsFile.loadOperatorsFile(name);
      if(ofile != null) {
        IRSyntax irsyn = ofile.read(root);
        if(irsyn != null) {
          opool.addSyntax(irsyn);
          if(irsyn.hasBaseSyntax()) ret &= loadOperatorsFile(irsyn.getBaseSyntax(), ir);

          for(String mixin : irsyn.getMixinSyntax()) ret &= loadOperatorsFile(mixin, ir);
        }
        else ret = false;
      }
      else ret = false;
    }

    return ret;
  }

  private CtClass[] getTypes(List<String> typeNames, TypeResolver resolver) throws NotFoundError {
    CtClass[] types = new CtClass[typeNames.size()];
    for(int i = 0; i < types.length; i++) {
      types[i] = resolver.getType(typeNames.get(i));
    }
    return types;
  }

  private String[] getParamNames(List<Parameter> params) {
    String[] paramNames = new String[params.size()];
    for(int i = 0; i < paramNames.length; i++) {
      paramNames[i] = params.get(i).getName();
    }
    return paramNames;
  }

  private CtClass[] getParamTypes(List<Parameter> params, TypeResolver resolver) throws NotFoundError {
    CtClass[] paramTypes = new CtClass[params.size()];
    for(int i = 0; i < paramTypes.length; i++) {
      paramTypes[i] = resolver.getType(params.get(i).getType());
    }
    return paramTypes;
  }

  private CtClass[] getAndPredicateTypes(OperatorPattern pattern, TypeResolver resolver) throws NotFoundError {
    CtClass[] preds = new CtClass[pattern.getAndPredicateLength()];
    for(int i = 0, j = 0; i < pattern.getLength(); i++) {
      if(pattern.isAndPredicate(i)) preds[j++] = resolver.getType(pattern.getPredicateTypeName(i));
    }
    return preds;
  }

  private CtClass[] getNotPredicateTypes(OperatorPattern pattern, TypeResolver resolver) throws NotFoundError {
    CtClass[] preds = new CtClass[pattern.getNotPredicateLength()];
    for(int i = 0, j = 0; i < pattern.getLength(); i++) {
      if(pattern.isNotPredicate(i)) preds[j++] = resolver.getType(pattern.getPredicateTypeName(i));
    }
    return preds;
  }

  private boolean lastParamIsVarArgs(List<Parameter> params) {
    return !params.isEmpty() && hasVarArgs(params.get(params.size() - 1).getModifiers());
  }

  private IROperandAttribute[] getParamModifiers(OperatorPattern pattern, List<Parameter> params) {
    IROperandAttribute[] paramMods = new IROperandAttribute[params.size()];

    int j = 0;
    for(int i = 0; i < pattern.getLength(); i++) {
      if(pattern.isOperand(i)) {
        Operand operand = (Operand)pattern.get(i);
        int mod = params.get(j).getModifiers();

        if(hasVarArgs(mod)) {
          switch(operand.getOption()) {
            case Operand.OPTION_STAR : break;
            case Operand.OPTION_PLUS : mod = ( mod & ~Modifiers.VARARGS ) | Modifiers.PLUSARGS; break;
            default : mod &= ~Modifiers.VARARGS; break;
          }
        }

        if(operand.isOption() && params.get(j).hasDefaultValue()) {
          mod |= Modifiers.OPTION;
        }

        if(operand.hasOptionArg()) {
          paramMods[j] = new IROperandAttribute(mod, operand.getOptionArg());
        }
        else {
          paramMods[j] = new IROperandAttribute(mod);
        }
        j++;
      }
    }

    return paramMods;
  }

  private String appendPackageName(String packageName, String shortName) {
    if(packageName.equals("")) return shortName;
    else return packageName + '.' + shortName;
  }

  private Collection<CompilationUnit> cunits;
  private final RootTypeResolver root;
}

