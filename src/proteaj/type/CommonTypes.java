package proteaj.type;

import javassist.CtClass;
import proteaj.error.ErrorList;
import proteaj.error.NotFoundError;
import proteaj.error.Warning;

public class CommonTypes {
  public final CtClass nullType;
  public final CtClass stringType;
  public final CtClass objectType;
  public final CtClass classType;
  public final CtClass throwableType;
  public final CtClass errorType;
  public final CtClass runtimeExceptionType;
  public final CtClass identifierType;
  public final CtClass letterType;
  public final CtClass typeType;

  public static CommonTypes getInstance() {
    if (instance == null) instance = new CommonTypes();
    return instance;
  }

  private CommonTypes () {
    RootTypeResolver root = RootTypeResolver.getInstance();

    try {
      nullType             = root.makeClass("$null");
      stringType           = root.getType("java.lang.String");
      objectType           = root.getType("java.lang.Object");
      classType            = root.getType("java.lang.Class");
      throwableType        = root.getType("java.lang.Throwable");
      errorType            = root.getType("java.lang.Error");
      runtimeExceptionType = root.getType("java.lang.RuntimeException");
    } catch (NotFoundError e) {
      assert false;
      ErrorList.addError(e);
      throw new RuntimeException(e);
    }

    identifierType = loadType(root, "proteaj.lang.Identifier");
    letterType     = loadType(root, "proteaj.lang.Letter");
    typeType       = loadType(root, "proteaj.lang.Type");
  }

  private CtClass loadType (RootTypeResolver root, String name) {
    CtClass clazz = root.getTypeOrNull(name);
    if (clazz == null) Warning.print(name + " is not found");
    return clazz;
  }

  private static CommonTypes instance = null;
}
