package proteaj.ir;

import proteaj.error.*;
import proteaj.ir.*;

import java.util.*;
import javassist.*;

public class TypeResolver {
  public TypeResolver(IRHeader hdata, ClassPool cpool) {
    this.cpool = cpool;
    this.hdata = hdata;
  }

  public boolean isTypeName(String name) {
    if(primTypes.containsKey(name)) return true;
    if(name.contains(".")) return cpool.getOrNull(name) != null;
    if(hdata.containsAbbName(name)) return true;

    CtClass ctcl = cpool.getOrNull(appendPackageName(name));
    if(ctcl != null) {
      String lname = appendPackageName(name);
      hdata.addAbbName(name, lname);
      return true;
    }

    for(String pack : hdata.getImportPackages()) {
      ctcl = cpool.getOrNull(pack + '.' + name);

      if(ctcl != null) {
        String lname = pack + '.' + name;
        hdata.addAbbName(name, lname);
        return true;
      }
    }

    return false;
  }

  public CtClass getType(String name) throws NotFoundError {
    if(primTypes.containsKey(name)) {
      return primTypes.get(name);
    }
    if(hdata.containsAbbName(name)) try {
      return cpool.get(hdata.getLongName(name));
    } catch (NotFoundException e) {
      throw new NotFoundError("class " + name + " is not found", hdata.getFilePath(), 0);
    }
    if(name.contains(".")) {
      CtClass ctcl = cpool.getOrNull(name);
      if(ctcl != null) return ctcl;
    }
    if(isArray(name)) try {
      int pos = name.indexOf('[');
      String cname = name.substring(0, pos);
      String array = name.substring(pos);
      CtClass component = getType(cname);
      String lname = component.getName() + array;

      hdata.addAbbName(name, lname);
      return cpool.get(lname);
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException("array type " + name + " is not found");
    }

    CtClass ctcl = cpool.getOrNull(appendPackageName(name));
    if(ctcl != null) {
      String lname = appendPackageName(name);
      hdata.addAbbName(name, lname);
      return ctcl;
    }

    for(String pack : hdata.getImportPackages()) {
      ctcl = cpool.getOrNull(pack + '.' + name);

      if(ctcl != null) {
        String lname = pack + '.' + name;
        hdata.addAbbName(name, lname);
        return ctcl;
      }
    }


    throw new NotFoundError("class " + name + " is not found", hdata.getFilePath(), 0);
  }

  private String appendPackageName(String name) {
    String packageName = hdata.getPackageName();
    if(packageName.equals("")) return name;
    else return packageName + '.' + name;
  }

  private boolean isArray(String name) {
    return name.endsWith("[]");
  }

  private ClassPool cpool;
  private IRHeader hdata;

  private static final Map<String, CtClass> primTypes = new HashMap<String, CtClass>();

  static {
    primTypes.put("boolean", CtClass.booleanType);
    primTypes.put("byte", CtClass.byteType);
    primTypes.put("char", CtClass.charType);
    primTypes.put("double", CtClass.doubleType);
    primTypes.put("float", CtClass.floatType);
    primTypes.put("int", CtClass.intType);
    primTypes.put("long", CtClass.longType);
    primTypes.put("short", CtClass.shortType);
    primTypes.put("void", CtClass.voidType);
  }
}

