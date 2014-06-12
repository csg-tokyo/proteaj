package proteaj;

import proteaj.env.type.RootTypeResolver;
import proteaj.error.*;
import proteaj.ir.IR;
import proteaj.tast.Program;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Compiler {
  public static final String version = "1.4.0.0";

  public static void main(String[] argsArray) {
    CommandLineArgs args = CommandLineArgs.parse(argsArray);

    if (args.failedToParse()) args.printFailMessage();
    else if (args.usageFlag) args.printUsage();
    else if (args.versionFlag) System.out.println("ProteaJ compiler version " + version);
    else if (args.hasArgs()) new Compiler(args).compile();
    else args.printUsage();
  }

  private void compile() {
    if (args.isDebugMode) ForDebug.setVerboseFlag();

    if (! args.additionalClassPath.isEmpty()) try {
      RootTypeResolver.getInstance().appendClassPath(args.additionalClassPath);
    } catch (NotFoundError e) {
      System.err.println("invalid class path : " + args.additionalClassPath);
      return;
    }

    ErrorList.init();

    try {
      List<File> files = args.getArgs().stream().map(File::new).collect(Collectors.toList());
      IR ir = new SigCompiler().compile(files);
      Program program = new BodyCompiler(ir).compile();

      if (! ErrorList.hasError()) new CodeGenerator(args.destination, args.translateToJava).codegen(program);
    } finally {
      if(ErrorList.hasError()) {
        ErrorList.printAllErrors();
      }
    }
  }

  private Compiler (CommandLineArgs args) {
    this.args = args;
  }

  private final CommandLineArgs args;
}
