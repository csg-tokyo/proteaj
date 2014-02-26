package proteaj;

import proteaj.error.*;
import proteaj.ast.*;
import proteaj.io.*;
import proteaj.ir.*;
import proteaj.util.*;

import java.util.*;
import javassist.*;

import static proteaj.util.Modifiers.*;

public class SigIRGenerator {
  public SigIRGenerator(Collection<CompilationUnit> cunits) {
    this.cunits = cunits;
  }

  public IR generateIR() {
    IR ir = new IR();

    Collection<Pair<IRHeader, FileBody>> files = analyzeHeaders(cunits, ir);
    Collection<Pair<CtClass, ClassDecl>> classes = registerClasses(files, ir);
    Collection<Pair<CtClass, InterfaceDecl>> interfaces = registerInterfaces(files, ir);
    Collection<Pair<CtClass, SyntaxDecl>> syntax = registerSyntax(files, ir);

    registerCommonTypes(ir);
    loadPrimitiveOperators(ir);

    registerSuperClass(classes, ir);
    registerStaticInitializer(classes, ir);
    registerConstructor(classes, ir);
    registerMethod(classes, ir);
    registerField(classes, ir);
    registerSuperInterface(interfaces, ir);
    registerInterfaceMethod(interfaces, ir);
    registerInterfaceField(interfaces, ir);
    registerOperator(syntax, ir);

    return ir;
  }

  private Collection<Pair<IRHeader, FileBody>> analyzeHeaders(Collection<CompilationUnit> cunits, IR ir) {
    Collection<Pair<IRHeader, FileBody>> files = new ArrayList<Pair<IRHeader,FileBody>>();

    ClassPool cpool = ir.getClassPool();

    for(CompilationUnit cunit : cunits) {
      FileHeader header = cunit.getHeader();
      FileBody body = cunit.getBody();

      IRHeader hdata = new IRHeader(cunit.getFilePath(), header.getPackName(),
          header.getImportPackages(), header.getUsingSyntax(), header.getUnusingSyntax());

      for(String icls : header.getImportClasses()) {
        hdata.addAbbName(getShortName(icls), icls);
      }

      for(String pack : header.getImportPackages()) {
        cpool.importPackage(pack);
      }

      files.add(new Pair<IRHeader, FileBody>(hdata, body));
    }

    return files;
  }

  private Collection<Pair<CtClass, ClassDecl>> registerClasses(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Collection<Pair<CtClass, ClassDecl>> classes = new ArrayList<Pair<CtClass,ClassDecl>>();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader hdata = pair.getFirst();
      FileBody body = pair.getSecond();
      String packageName = hdata.getPackageName();

      for(ClassDecl cdecl : body.getClasses()) {
        String shortName = cdecl.getName();
        String longName = appendPackageName(packageName, shortName);

        CtClass ctcl = ir.makeClass(longName, cdecl.getModifiers());
        ir.addClass(ctcl, hdata);

        hdata.addAbbName(shortName, longName);
        classes.add(new Pair<CtClass, ClassDecl>(ctcl, cdecl));
      }
    }

    return classes;
  }

  private Collection<Pair<CtClass, InterfaceDecl>> registerInterfaces(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Collection<Pair<CtClass, InterfaceDecl>> ifaces = new ArrayList<Pair<CtClass,InterfaceDecl>>();
    ClassPool cpool = ir.getClassPool();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader hdata = pair.getFirst();
      FileBody body = pair.getSecond();
      String packageName = hdata.getPackageName();

      for(InterfaceDecl idecl : body.getInterfaces()) {
        String shortName = idecl.getName();
        String longName = appendPackageName(packageName, shortName);

        CtClass iface = cpool.makeInterface(longName);
        iface.setModifiers(iface.getModifiers() | idecl.getModifiers());
        ir.addInterface(iface, hdata);

        hdata.addAbbName(shortName, longName);
        ifaces.add(new Pair<CtClass, InterfaceDecl>(iface, idecl));
      }
    }

    return ifaces;
  }

  private Collection<Pair<CtClass, SyntaxDecl>> registerSyntax(Collection<Pair<IRHeader, FileBody>> files, IR ir) {
    Collection<Pair<CtClass, SyntaxDecl>> syntax = new ArrayList<Pair<CtClass,SyntaxDecl>>();

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader header = pair.getFirst();

      for(String ops : header.getUsingSyntax()) try {
        loadOperatorsFile(ops, ir);
      } catch (FileIOError e) {
        ErrorList.addError(e);
      }
    }

    for(Pair<IRHeader, FileBody> pair : files) {
      IRHeader hdata = pair.getFirst();
      FileBody body = pair.getSecond();
      String packageName = hdata.getPackageName();

      for(SyntaxDecl syn : body.getSyntax()) {
        String longName = appendPackageName(packageName, syn.getName());
        CtClass ctcl = ir.makeClass(longName, syn.getModifiers());
        ir.addClass(ctcl, hdata);
        syntax.add(new Pair<CtClass, SyntaxDecl>(ctcl, syn));
      }
    }

    return syntax;
  }

  private void registerCommonTypes(IR ir) {
    IRCommonTypes.init(ir.getClassPool());
  }

  private void loadPrimitiveOperators(IR ir) {
    ir.getOperatorPool().loadPrimitiveOperators();
  }

  private void registerSuperClass(Collection<Pair<CtClass, ClassDecl>> classes, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, ClassDecl> pair : classes) try {
      CtClass ctcl = pair.getFirst();
      ClassDecl cdecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(ctcl), cpool);

      ctcl.setSuperclass(resolver.getType(cdecl.getSuperClass()));
      for(String iface : cdecl.getInterfaces()) {
        ctcl.addInterface(resolver.getType(iface));
      }
    } catch (NotFoundError e) {
      ErrorList.addError(e);
    } catch (CannotCompileException e) {
      ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(pair.getFirst()).getFilePath(), pair.getSecond().getLine()));
    }
  }

  private void registerStaticInitializer(Collection<Pair<CtClass, ClassDecl>> classes, IR ir) {
    for(Pair<CtClass, ClassDecl> pair : classes) {
      CtClass ctcl = pair.getFirst();
      ClassDecl cdecl = pair.getSecond();

      for(StaticInitializer sinit : cdecl.getStaticInitializers()) try {
        IRStaticInitializer irsinit = new IRStaticInitializer(ctcl.makeClassInitializer(), sinit.getBody(), sinit.getBodyLine());
        ir.addStaticInitializer(irsinit);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(ctcl).getFilePath(), sinit.getLine()));
      }
    }
  }

  private void registerConstructor(Collection<Pair<CtClass, ClassDecl>> classes, IR ir) {
    ClassPool cpool = ir.getClassPool();
    for(Pair<CtClass, ClassDecl> pair : classes) {
      CtClass ctcl = pair.getFirst();
      ClassDecl cdecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(ctcl), cpool);

      // default constructor
      if(cdecl.getConstructors().isEmpty()) try {
        ctcl.addConstructor(CtNewConstructor.defaultConstructor(ctcl));
        continue;
      } catch (CannotCompileException e) {
        assert false;
        throw new RuntimeException(e);
      }

      for(ConstructorDecl constructor : cdecl.getConstructors()) try {
        CtClass[] params = getParamTypes(constructor.getParams(), resolver);
        int mods = constructor.getModifiers();
        if(lastParamIsVarArgs(constructor.getParams())) mods |= Modifiers.VARARGS;

        CtConstructor ctconstructor = new CtConstructor(params, ctcl);
        ctconstructor.setModifiers(mods);

        if(constructor.hasThrowsException()) try {
          ctconstructor.setExceptionTypes(getExceptionTypes(constructor.getThrowsExceptions(), resolver));
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, ir.getIRHeader(ctcl).getFilePath(), constructor.getLine()));
        }

        if(constructor.hasBody()) {
          ir.addConstructor(new IRConstructorBody(ctconstructor, getParamNames(constructor.getParams()), constructor.getBody(), constructor.getBodyLine()));
        }

        ctcl.addConstructor(ctconstructor);
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(ctcl).getFilePath(), constructor.getLine()));
      }
    }
  }

  private void registerMethod(Collection<Pair<CtClass, ClassDecl>> classes, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, ClassDecl> pair : classes) {
      CtClass ctcl = pair.getFirst();
      ClassDecl cdecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(ctcl), cpool);

      for(MethodDecl method : cdecl.getMethods()) try {
        CtClass returnType = resolver.getType(method.getReturnType());
        CtClass[] paramTypes = getParamTypes(method.getParams(), resolver);
        int mods = method.getModifiers();
        if(lastParamIsVarArgs(method.getParams())) mods |= Modifiers.VARARGS;

        CtMethod ctmethod = new CtMethod(returnType, method.getName(), paramTypes, ctcl);
        ctmethod.setModifiers(mods);

        if(method.hasThrowsException()) try {
          ctmethod.setExceptionTypes(getExceptionTypes(method.getThrowsExceptions(), resolver));
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, ir.getIRHeader(ctcl).getFilePath(), method.getLine()));
        }

        ctcl.addMethod(ctmethod);

        if(method.hasBody()) {
          ir.addMethod(new IRMethodBody(ctmethod, getParamNames(method.getParams()), method.getBody(), method.getBodyLine()));
        }
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(ctcl).getFilePath(), method.getLine()));
      }
    }
  }

  private void registerField(Collection<Pair<CtClass, ClassDecl>> classes, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, ClassDecl> pair : classes) {
      CtClass ctcl = pair.getFirst();
      ClassDecl cdecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(ctcl), cpool);

      for(FieldDecl field : cdecl.getFields()) try {
        CtField ctfield = new CtField(resolver.getType(field.getType()), field.getName(), ctcl);
        ctfield.setModifiers(field.getModifiers());

        ctcl.addField(ctfield);

        if(field.hasBody()) {
          ir.addField(new IRFieldBody(ctfield, field.getBody(), field.getBodyLine()));
        }
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(ctcl).getFilePath(), field.getLine()));
      }
    }
  }

  private void registerSuperInterface(Collection<Pair<CtClass, InterfaceDecl>> interfaces, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, InterfaceDecl> pair : interfaces) {
      CtClass iface = pair.getFirst();
      InterfaceDecl idecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(iface), cpool);

      for(String ifaceName : idecl.getInterfaces()) try {
        iface.addInterface(resolver.getType(ifaceName));
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      }
    }
  }

  private void registerInterfaceMethod(Collection<Pair<CtClass, InterfaceDecl>> interfaces, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, InterfaceDecl> pair : interfaces) {
      CtClass iface = pair.getFirst();
      InterfaceDecl idecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(iface), cpool);

      for(MethodDecl method : idecl.getMethods()) try {
        CtClass returnType = resolver.getType(method.getReturnType());
        CtClass[] paramTypes = getParamTypes(method.getParams(), resolver);
        int mods = method.getModifiers() | Modifiers.PUBLIC | Modifiers.ABSTRACT;
        if(lastParamIsVarArgs(method.getParams())) mods |= Modifiers.VARARGS;

        CtMethod ctmethod = new CtMethod(returnType, method.getName(), paramTypes, iface);
        ctmethod.setModifiers(mods);
        iface.addMethod(ctmethod);
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(iface).getFilePath(), method.getLine()));
      }
    }
  }

  private void registerInterfaceField(Collection<Pair<CtClass, InterfaceDecl>> interfaces, IR ir) {
    ClassPool cpool = ir.getClassPool();

    for(Pair<CtClass, InterfaceDecl> pair : interfaces) {
      CtClass iface = pair.getFirst();
      InterfaceDecl idecl = pair.getSecond();
      TypeResolver resolver = new TypeResolver(ir.getIRHeader(iface), cpool);

      for(FieldDecl field : idecl.getFields()) try {
        CtField ctfield = new CtField(resolver.getType(field.getType()), field.getName(), iface);
        ctfield.setModifiers(field.getModifiers() | Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);

        iface.addField(ctfield);

        assert field.hasBody();
        ir.addField(new IRFieldBody(ctfield, field.getBody(), field.getBodyLine()));
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(iface).getFilePath(), field.getLine()));
      }
    }
  }

  private void registerOperator(Collection<Pair<CtClass, SyntaxDecl>> operators, IR ir) {
    ClassPool cpool = ir.getClassPool();
    OperatorPool opool = ir.getOperatorPool();

    for(Pair<CtClass, SyntaxDecl> pair : operators) {
      CtClass ctcl = pair.getFirst();
      SyntaxDecl syndecl = pair.getSecond();

      TypeResolver resolver = new TypeResolver(ir.getIRHeader(ctcl), cpool);
      IRSyntax irsyn = new IRSyntax(ctcl);

      if(syndecl.hasBaseOperators()) try {
        String base = syndecl.getBaseOperators();
        loadOperatorsFile(base, ir);
        irsyn.setBaseSyntax(base);
      } catch (FileIOError e) {
        ErrorList.addError(e);
      }

      for(String mixin : syndecl.getMixinOperators()) try {
        loadOperatorsFile(mixin, ir);
        irsyn.addMixinSyntax(mixin);
      } catch (FileIOError e) {
        ErrorList.addError(e);
      }

      for(OperatorDecl odecl : syndecl.getOperators()) try {
        String mname = odecl.hasName() ? odecl.getName() : ctcl.makeUniqueName("$ProteaJAutoGeneratedMethod");
        OperatorPattern pattern = odecl.getPattern();
        CtClass[] paramTypes = getParamTypes(odecl.getParams(), resolver);
        IROperandAttribute[] paramMods = getParamModifiers(pattern, odecl.getParams());
        CtClass[] andPreds = getAndPredicateTypes(pattern, resolver);
        CtClass[] notPreds = getNotPredicateTypes(pattern, resolver);
        CtClass returnType = resolver.getType(odecl.getType());
        int modifiers = odecl.getModifiers() | Modifiers.STATIC;

        registerDefaultArgument(paramMods, paramTypes, odecl.getParams(), ctcl, ir);

        CtMethod smethod = new CtMethod(returnType, mname, paramTypes, ctcl);
        smethod.setModifiers(modifiers);

        if(odecl.hasThrowsException()) try {
          smethod.setExceptionTypes(getExceptionTypes(odecl.getThrowsExceptions(), resolver));
        } catch (NotFoundException e) {
          ErrorList.addError(new NotFoundError(e, ir.getIRHeader(ctcl).getFilePath(), odecl.getLine()));
        }

        ctcl.addMethod(smethod);

        IROperator odata = new IROperator(modifiers, returnType, pattern, paramTypes, paramMods, andPreds, notPreds, odecl.getPriority(), smethod);
        irsyn.addOperator(odata);

        if(odecl.hasBody()) {
          ir.addMethod(new IRMethodBody(smethod, getParamNames(odecl.getParams()), odecl.getBody(), odecl.getBodyLine()));
        }
      } catch (NotFoundError e) {
        ErrorList.addError(e);
      } catch (CannotCompileException e) {
        ErrorList.addError(new SemanticsError(e.getMessage(), ir.getIRHeader(ctcl).getFilePath(), odecl.getLine()));
      }

      opool.addSyntax(irsyn);
      ir.addSyntax(irsyn);
    }
  }

  private void registerDefaultArgument(IROperandAttribute[] attrs, CtClass[] types, List<Parameter> params, CtClass ctcl, IR ir) {
    assert attrs.length == types.length;
    assert attrs.length == params.size();

    for(int i = 0; i < attrs.length; i++) {
      if(attrs[i].isOption()) try {
        Parameter param = params.get(i);
        assert param.hasDefaultValue();

        String mname = ctcl.makeUniqueName("$ProteaJAutoGeneratedMethod");
        CtMethod method = new CtMethod(types[i], mname, new CtClass[0], ctcl);
        method.setModifiers(Modifiers.PUBLIC | Modifiers.STATIC);

        ctcl.addMethod(method);
        attrs[i].setDefaultMethod(method);
        ir.addDefaultArgument(new IRDefaultArgument(method, param.getDefaultValue(), param.getDefaultValueLine()));

      } catch (CannotCompileException e) {
        assert false;
        throw new RuntimeException(e);
      }
    }
  }

  private boolean loadOperatorsFile(String name, IR ir) throws FileIOError {
    ClassPool cpool = ir.getClassPool();
    OperatorPool opool = ir.getOperatorPool();

    boolean ret = true;

    if(! opool.containsSyntax(name)) {
      OperatorsFile ofile = OperatorsFile.loadOperatorsFile(name);
      if(ofile != null) {
        IRSyntax irsyn = ofile.read(cpool);
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

  private CtClass[] getExceptionTypes(List<String> exceptions, TypeResolver resolver) throws NotFoundError {
    CtClass[] eTypes = new CtClass[exceptions.size()];
    for(int i = 0; i < eTypes.length; i++) {
      eTypes[i] = resolver.getType(exceptions.get(i));
    }
    return eTypes;
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
    if(params.isEmpty()) return false;
    return hasVarArgs(params.get(params.size() - 1).getModifiers());
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

  private String getShortName(String longName) {
    if(longName.contains(".")) return longName.substring(longName.lastIndexOf('.') + 1);
    else return longName;
  }

  private Collection<CompilationUnit> cunits;
}

