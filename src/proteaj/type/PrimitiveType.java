package proteaj.type;

import javassist.CtClass;

public class PrimitiveType implements Type {
  public static PrimitiveType booleanType = new PrimitiveType(CtClass.booleanType);
  public static PrimitiveType byteType    = new PrimitiveType(CtClass.byteType);
  public static PrimitiveType charType    = new PrimitiveType(CtClass.charType);
  public static PrimitiveType doubleType  = new PrimitiveType(CtClass.doubleType);
  public static PrimitiveType floatType   = new PrimitiveType(CtClass.floatType);
  public static PrimitiveType intType     = new PrimitiveType(CtClass.intType);
  public static PrimitiveType longType    = new PrimitiveType(CtClass.longType);
  public static PrimitiveType shortType   = new PrimitiveType(CtClass.shortType);
  public static PrimitiveType voidType    = new PrimitiveType(CtClass.voidType);

  @Override
  public String getName() {
    return clazz.getName();
  }

  private PrimitiveType (CtClass clazz) { this.clazz = clazz; }

  private final CtClass clazz;
}
