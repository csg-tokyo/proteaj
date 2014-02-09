package proteaj;

import proteaj.error.ErrorList;
import proteaj.ir.*;

import java.util.*;
import java.io.File;

public class Compiler {
  public static void main(String[] args) {
    new Compiler().compile(args);
  }

  public void compile(String... files) {
    List<File> fileList = new ArrayList<File>();
    for(String file : files) {
      fileList.add(new File(file));
    }
    compile(fileList);
  }

  public void compile(Collection<File> files) {
    ErrorList.init();

    IR ir = new SigCompiler().compile(files);
    ir = new BodyCompiler(ir).compile();
    new CodeGenerator("./bin").codegen(ir);

    if(ErrorList.hasError()) {
      ErrorList.printAllErrors();
    }
  }

  public static final String version = "1.1.1.0";
}
