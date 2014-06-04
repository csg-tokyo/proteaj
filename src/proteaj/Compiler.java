package proteaj;

import proteaj.error.*;
import proteaj.ir.IR;
import proteaj.tast.Program;

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
    //ForDebug.setVerboseFlag();
    ErrorList.init();

    try {
      IR ir = new SigCompiler().compile(files);
      Program program = new BodyCompiler(ir).compile();

      if (! ErrorList.hasError()) new CodeGenerator("./bin").codegen(program);
    } finally {
      if(ErrorList.hasError()) {
        ErrorList.printAllErrors();
      }
    }
  }

  public static final String version = "1.3.0.0";
}
