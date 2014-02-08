package proteaj.ir;

import proteaj.error.*;

import javassist.*;

public class IRCommonTypes {
  public static CtClass getNullType() {
    return nullType;
  }

  public static CtClass getStringType() {
    return stringType;
  }

  public static CtClass getObjectType() {
    return objectType;
  }

  public static CtClass getClassType() {
    return classType;
  }

  public static CtClass getThrowableType() {
    return throwableType;
  }

  public static CtClass getErrorType() {
    return errorType;
  }

  public static CtClass getRuntimeExceptionType() {
    return runtimeExceptionType;
  }

  public static CtClass getIdentifierType() {
    return identifierType;
  }

  public static CtClass getLetterType() {
    return letterType;
  }

  public static CtClass getTypeType() {
    return typeType;
  }

  public static void init(ClassPool cpool) {
    try {
      nullType = cpool.makeClass("$null");
      stringType = cpool.get("java.lang.String");
      objectType = cpool.get("java.lang.Object");
      classType = cpool.get("java.lang.Class");
      throwableType = cpool.get("java.lang.Throwable");
      errorType = cpool.get("java.lang.Error");
      runtimeExceptionType = cpool.get("java.lang.RuntimeException");
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    }

    identifierType = loadType(cpool, "proteaj.lang.Identifier");
    letterType = loadType(cpool, "proteaj.lang.Letter");
    typeType = loadType(cpool, "proteaj.lang.Type");
  }

  private static CtClass loadType(ClassPool cpool, String name) {
    CtClass type = cpool.getOrNull(name);
    if(type == null) Warning.print(name + " is not found.");
    return type;
  }

  private static CtClass nullType;
  private static CtClass stringType;
  private static CtClass objectType;
  private static CtClass classType;
  private static CtClass throwableType;
  private static CtClass errorType;
  private static CtClass runtimeExceptionType;
  private static CtClass identifierType;
  private static CtClass letterType;
  private static CtClass typeType;

}
