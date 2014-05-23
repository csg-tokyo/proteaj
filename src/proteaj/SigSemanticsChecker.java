package proteaj;

import proteaj.error.*;
import proteaj.ast.*;
import proteaj.util.*;

import java.util.*;

import static proteaj.util.Modifiers.*;

public class SigSemanticsChecker {
  public SigSemanticsChecker(CompilationUnit cunit) {
    this.cunit = cunit;
  }

  public boolean checkAll() {
    return checkCompilationUnit(cunit);
  }

  private boolean checkCompilationUnit(CompilationUnit cunit) {
    return checkFileHeader(cunit.header) && checkFileBody(cunit.body);
  }

  private boolean checkFileHeader(FileHeader header) {
    return true;
  }

  private boolean checkFileBody(FileBody body) {
    boolean ret = true;
    for(ClassDecl cdecl : body.getClasses()) ret &= checkClassDecl(cdecl);
    for(InterfaceDecl idecl : body.getInterfaces()) ret &= checkInterfaceDecl(idecl);
    for(SyntaxDecl sdecl : body.getSyntax()) ret &= checkSyntaxDecl(sdecl);
    return ret;
  }

  private boolean checkClassDecl(ClassDecl cdecl) {
    boolean ret = true;
    int mod = cdecl.getModifiers();
    int line = cdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~CLASS_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if((mod & PUBLIC) > 0) {
      int s = file.lastIndexOf('/');
      int e = file.lastIndexOf('.');

      String fileName = e == -1 ? s == -1 ? file : file.substring(s + 1) : file.substring(s + 1, e);

      if(! cdecl.getName().equals(fileName)) {
        ErrorList.addError(new SemanticsError("The public type " + cdecl.getName() + " must be defined in its own file", file, line));
        ret = false;
      }
    }

    for(ConstructorDecl constructor : cdecl.getConstructors()) ret &= checkConstructorDecl(constructor, cdecl);
    for(FieldDecl field : cdecl.getFields()) ret &= checkFieldDecl(field);

    if((mod & ABSTRACT) > 0) {
      for(MethodDecl method : cdecl.getMethods()) ret &= checkMethodDecl(method);
    }
    else {
      for(MethodDecl method : cdecl.getMethods()) ret &= checkConcreteMethodDecl(method);
    }

    return ret;
  }

  private boolean checkInterfaceDecl(InterfaceDecl idecl) {
    boolean ret = true;
    int mod = idecl.getModifiers();
    int line = idecl.getLine();
    String file = cunit.filePath;

    if((mod & ~INTERFACE_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if((mod & PUBLIC) > 0) {
      int s = file.lastIndexOf('/');
      int e = file.lastIndexOf('.');

      String fileName = e == -1 ? s == -1 ? file : file.substring(s + 1) : file.substring(s + 1, e);

      if(! idecl.getName().equals(fileName)) {
        ErrorList.addError(new SemanticsError("The public type " + idecl.getName() + " must be defined in its own file", file, line));
        ret = false;
      }
    }

    for(FieldDecl field : idecl.getFields()) ret &= checkConstantDecl(field);
    for(MethodDecl method : idecl.getMethods()) ret &= checkAbstractMethodDecl(method);

    return ret;
  }

  private boolean checkSyntaxDecl(SyntaxDecl sdecl) {
    boolean ret = true;
    int mod = sdecl.getModifiers();
    int line = sdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~SYNTAX_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if((mod & PUBLIC) > 0) {
      int s = file.lastIndexOf('/');
      int e = file.lastIndexOf('.');

      String fileName = e == -1 ? s == -1 ? file : file.substring(s + 1) : file.substring(s + 1, e);

      if(! sdecl.getName().equals(fileName)) {
        ErrorList.addError(new SemanticsError("The public operators module " + sdecl.getName() + " must be defined in its own file", file, line));
        ret = false;
      }
    }

    for(OperatorDecl operator : sdecl.getOperators()) ret &= checkOperatorDecl(operator);

    return ret;
  }

  private boolean checkConstructorDecl(ConstructorDecl cdecl, ClassDecl clz) {
    boolean ret = true;
    int mod = cdecl.getModifiers();
    int line = cdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~CONSTRUCTOR_MODIFIER) > 0 || ! isValidAccessModifier(mod)) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if(! cdecl.name.equals(clz.getName())) {
      ErrorList.addError(new SemanticsError("invalid constructor name : " + cdecl.name, file, line));
      ret = false;
    }

    for(Parameter param : cdecl.params) ret &= checkMethodParameter(param);

    return ret;
  }

  private boolean checkFieldDecl(FieldDecl fdecl) {
    int mod = fdecl.getModifiers();
    int line = fdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~FIELD_MODIFIER) > 0 || ! isValidAccessModifier(mod)) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      return false;
    }

    return true;
  }

  private boolean checkMethodDecl(MethodDecl mdecl) {
    boolean ret = true;
    int mod = mdecl.getModifiers();
    int line = mdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~METHOD_MODIFIER) > 0 || ! isValidAccessModifier(mod) || (((mod & ABSTRACT) > 0) && ((mod & PRIVATE) > 0))) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if((mod & ABSTRACT) > 0 && mdecl.hasBody()) {
      ErrorList.addError(new SemanticsError("abstract method does not specify a body", file, line));
      ret = false;
    }

    for(Parameter param : mdecl.params) ret &= checkMethodParameter(param);

    return ret;
  }

  private boolean checkConcreteMethodDecl(MethodDecl mdecl) {
    boolean ret = true;
    int mod = mdecl.getModifiers();
    int line = mdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~CONCRETE_METHOD_MODIFIER) > 0 || ! isValidAccessModifier(mod)) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    for(Parameter param : mdecl.params) ret &= checkMethodParameter(param);

    return ret;
  }

  private boolean checkConstantDecl(FieldDecl fdecl) {
    boolean ret = true;
    int mod = fdecl.getModifiers();
    int line = fdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~CONSTANT_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if(! fdecl.hasBody()) {
      ErrorList.addError(new SemanticsError("uninitialized constant : " + fdecl.name, file, line));
      ret = false;
    }

    return ret;
  }

  private boolean checkAbstractMethodDecl(MethodDecl mdecl) {
    boolean ret = true;
    int mod = mdecl.getModifiers();
    int line = mdecl.getLine();
    String file = cunit.filePath;

    if((mod & ~ABSTRACT_METHOD_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if(mdecl.hasBody()) {
      ErrorList.addError(new SemanticsError("abstract method does not specify a body", file, line));
      ret = false;
    }

    for(Parameter param : mdecl.params) ret &= checkMethodParameter(param);

    return ret;
  }

  private boolean checkOperatorDecl(OperatorDecl odecl) {
    boolean ret = true;
    int mod = odecl.getModifiers();
    int line = odecl.getLine();
    String file = cunit.filePath;

    if((mod & ~OPERATOR_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if(! odecl.hasBody()) {
      ErrorList.addError(new SemanticsError("can't define an abstract operator in the current version of ProteaJ", file, line));
    }

    ret &= checkOperatorPattern(odecl.pattern, odecl.params);

    return ret;
  }

  private boolean checkMethodParameter(Parameter param) {
    int mod = param.getModifiers();
    int line = param.getLine();
    String file = cunit.filePath;

    if((mod & ~PARAMETER_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      return false;
    }
    if(param.hasDefaultValue()) {
      ErrorList.addError(new SemanticsError("default argument is not available in method/constructor declarations", file, line));
      return false;
    }
    return true;
  }

  private boolean checkOperatorPattern(OperatorPattern pattern, List<Parameter> params) {
    boolean ret = true;
    int length = pattern.getLength();
    int line = pattern.getLine();
    String file = cunit.filePath;

    Deque<Integer> stack = new ArrayDeque<>();

    for(int i = 0, j = 0; i < length; i++) {
      if(pattern.isOperand(i)) {
        if(params.size() <= j) {
          ErrorList.addError(new SemanticsError("corresponding parameter is not found", file, line));
          ret = false;
          continue;
        }

        ret &= checkOperand((Operand) pattern.get(i), params.get(j++));
      }
      else if(pattern.isOperator(i)) {
        String operator = pattern.getOperator(i);

        if(operator.contains("\'") || operator.contains("\"")) {
          ErrorList.addError(new SemanticsError("operator can't contain \" and \'", file, line));
          ret = false;
        }

        for(int k = 0; k < operator.length(); k++) {
          int c = operator.codePointAt(k);

          switch(c) {
            case '(': case '{': case '[':
              stack.addFirst(c);
              break;
            case ')': case '}': case ']':
              if(stack.peekFirst() != Brackets.getLeft(c)) {
                ErrorList.addError(new SemanticsError("uncorresponding '" + stack.peekFirst() + "'", file, line));
                ret = false;
              }
              stack.removeFirst();
              break;
          }
        }
      }
    }

    if(! stack.isEmpty()) {
      for(int i : stack) {
        ErrorList.addError(new SemanticsError("uncorresponding '" + (char)i + "'", file, line));
      }
      ret = false;
    }

    return ret;
  }

  private boolean checkOperand(Operand operand, Parameter param) {
    boolean ret = true;
    int mod = param.getModifiers();
    int line = operand.getLine();
    String file = cunit.filePath;

    if((mod & ~OPERAND_MODIFIER) > 0) {
      ErrorList.addError(new SemanticsError("invalid modifier : " + Modifiers.toString(mod), file, line));
      ret = false;
    }

    if(! operand.getName().equals(param.getName()) && ! operand.getName().equals("_")) {
      ErrorList.addError(new SemanticsError("operand name \"" + operand.getName() + "\" do not match the parameter name \"" + param.getName() + "\"", file, line));
      ret = false;
    }

    if(! operand.hasVarArgs() && (param.getModifiers() & VARARGS) > 0) {
      ErrorList.addError(new SemanticsError("to use variable-length operand, please attach an star option or plus option to the operand", file, line));
      ret = false;
    }

    if(operand.hasVarArgs() && (param.getModifiers() & VARARGS) == 0) {
      ErrorList.addError(new SemanticsError("to use variable-length operand, please use \"...\"", file, line));
      ret = false;
    }

    if(! operand.isOption() && param.hasDefaultValue()) {
      ErrorList.addError(new SemanticsError("to use option operand, please attach an question option to the operand", file, line));
      ret = false;
    }

    if(operand.isOption() && ! param.hasDefaultValue()) {
      ErrorList.addError(new SemanticsError("to use option operand, default argument is required", file, line));
      ret = false;
    }

    return ret;
  }

  private boolean isValidAccessModifier(int mod) {
    boolean isPublic = (mod & PUBLIC) > 0;
    boolean isProtected = (mod & PROTECTED) > 0;
    boolean isPrivate = (mod & PRIVATE) > 0;

    return isPublic ? ! (isProtected || isPrivate)
        : ! (isProtected && isPrivate);
  }

  private CompilationUnit cunit;

  private static final int CLASS_MODIFIER = ABSTRACT | FINAL | PUBLIC | STRICT | PURE;
  private static final int INTERFACE_MODIFIER = ABSTRACT | PUBLIC | STRICT;
  private static final int SYNTAX_MODIFIER = PUBLIC | STRICT;
  private static final int CONSTRUCTOR_MODIFIER = PUBLIC | PROTECTED | PRIVATE;
  private static final int FIELD_MODIFIER = FINAL | PUBLIC | PROTECTED | PRIVATE | STATIC | TRANSIENT | VOLATILE;
  private static final int CONSTANT_MODIFIER = FINAL | PUBLIC | STATIC;
  private static final int METHOD_MODIFIER = ABSTRACT | FINAL | NATIVE | PUBLIC | PROTECTED | PRIVATE | STATIC | STRICT | SYNCHRONIZED;
  private static final int ABSTRACT_METHOD_MODIFIER = PUBLIC | ABSTRACT;
  private static final int CONCRETE_METHOD_MODIFIER = FINAL | NATIVE | PUBLIC | PROTECTED | PRIVATE | STATIC | STRICT | SYNCHRONIZED;
  private static final int OPERATOR_MODIFIER = NON_ASSOC | PUBLIC | READAS | RIGHT_ASSOC | STRICT | SYNCHRONIZED | PURE;
  private static final int OPERAND_MODIFIER = VARARGS | LAZY | PURE;
  private static final int PARAMETER_MODIFIER = FINAL | VARARGS;
}

