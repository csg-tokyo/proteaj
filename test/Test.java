
import java.util.*;
import javassist.*;
import proteaj.ir.*;

public class Test {
  public static void main (String[] args) throws Exception {
    ClassPool pool = ClassPool.getDefault();
    IRCommonTypes.init(pool);

    CtClass list = pool.get("java.util.ArrayList");
    CtClass string = pool.get("java.lang.String");

    IRHeader header = new IRHeader("hoge", "hoge", new ArrayList<>(), new ArrayList<>(), new HashSet<>());

    ClassResolver resolver = new ClassResolver(header, pool);

    CtClass strList = resolver.getParameterizedClass(list, Arrays.asList(string));


    System.out.println(strList);
  }
}
