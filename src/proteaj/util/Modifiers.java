package proteaj.util;

import java.util.*;
import javassist.*;

public class Modifiers {
  public static boolean isModifier(String mod) {
    return modifiers.containsKey(mod);
  }

  public static int getModifiersMask(String mod) {
    assert isModifier(mod);
    return modifiers.get(mod);
  }

  public static boolean isPublic(int mod) { return (mod & PUBLIC) == PUBLIC; }

  public static boolean isStatic(int mod) {
    return (mod & STATIC) == STATIC;
  }

  public static boolean isStatic(CtMember member) {
    return isStatic(member.getModifiers());
  }

  public static boolean hasVarArgs(int mod) {
    return (mod & VARARGS) == VARARGS;
  }

  public static boolean hasVarArgs(CtBehavior behavior) {
    return hasVarArgs(behavior.getModifiers());
  }

  public static boolean hasMoreThanOneArgs(int mod) {
    return (mod & PLUSARGS) == PLUSARGS;
  }

  public static boolean isReadas(int mod) {
    return (mod & READAS) == READAS;
  }

  public static boolean isLazy(int mod) {
    return (mod & LAZY) == LAZY;
  }

  public static boolean isOption(int mod) {
    return (mod & OPTION) == OPTION;
  }

  public static boolean isRightAssoc(int mod) {
    return (mod & RIGHT_ASSOC) == RIGHT_ASSOC;
  }

  public static boolean isNonAssoc(int mod) {
    return (mod & NON_ASSOC) == NON_ASSOC;
  }

  public static boolean isPure(int mod) {
    return (mod & PURE) == PURE;
  }

  public static String toString(int mod) {
    StringBuilder buf = new StringBuilder();
    boolean isEmpty = true;

    for(Map.Entry<Integer, String> entry : modNames.entrySet()) {
      if((mod & entry.getKey()) > 0) {
        if(isEmpty) {
          isEmpty = false;
          buf.append(entry.getValue());
        }
        else {
          buf.append(' ').append(entry.getValue());
        }
      }
    }

    return buf.toString();
  }

  public static final int PUBLIC       = 0x000001;
  public static final int PRIVATE      = 0x000002;
  public static final int PROTECTED    = 0x000004;
  public static final int STATIC       = 0x000008;
  public static final int FINAL        = 0x000010;
  public static final int SYNCHRONIZED = 0x000020;
  public static final int VOLATILE     = 0x000040;
  public static final int TRANSIENT    = 0x000080;
  public static final int VARARGS      = 0x000080;
  public static final int NATIVE       = 0x000100;
  public static final int PLUSARGS     = 0x000100;
  public static final int INTERFACE    = 0x000200;
  public static final int OPTION       = 0x000200;
  public static final int ABSTRACT     = 0x000400;
  public static final int STRICT       = 0x000800;
  public static final int READAS       = 0x001000;
  public static final int ANNOTATION   = 0x002000;
  public static final int ENUM         = 0x004000;
  public static final int LAZY         = 0x008000;
  public static final int RIGHT_ASSOC  = 0x010000;
  public static final int NON_ASSOC    = 0x020000;
  public static final int PURE         = 0x040000;

  private static final Map<String, Integer> modifiers = new HashMap<String, Integer>();
  private static final Map<Integer, String> modNames = new HashMap<Integer, String>();

  static {
    modifiers.put("public", PUBLIC);
    modifiers.put("protected", PROTECTED);
    modifiers.put("private", PRIVATE);
    modifiers.put("abstract", ABSTRACT);
    modifiers.put("native", NATIVE);
    modifiers.put("static", STATIC);
    modifiers.put("final", FINAL);
    modifiers.put("synchronized", SYNCHRONIZED);
    modifiers.put("transient", TRANSIENT);
    modifiers.put("volatile", VOLATILE);
    modifiers.put("strictfp", STRICT);
    modifiers.put("readas", READAS);
    modifiers.put("lazy", LAZY);
    modifiers.put("rassoc", RIGHT_ASSOC);
    modifiers.put("nonassoc", NON_ASSOC);
    modifiers.put("pure", PURE);

    modNames.put(PUBLIC, "public");
    modNames.put(PROTECTED, "protected");
    modNames.put(PRIVATE, "private");
    modNames.put(ABSTRACT, "abstract");
    modNames.put(NATIVE, "native");
    modNames.put(STATIC, "static");
    modNames.put(FINAL, "final");
    modNames.put(SYNCHRONIZED, "synchronized");
    modNames.put(TRANSIENT, "transient");
    modNames.put(VOLATILE, "volatile");
    modNames.put(STRICT, "strictfp");
    modNames.put(READAS, "readas");
    modNames.put(LAZY, "lazy");
    modNames.put(RIGHT_ASSOC, "rassoc");
    modNames.put(NON_ASSOC, "nonassoc");
    modNames.put(PURE, "pure");
  }
}

