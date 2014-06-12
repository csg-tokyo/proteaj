package proteaj;

import org.kohsuke.args4j.*;

import java.util.*;

public class CommandLineArgs {
  @Option(name = "-d", metaVar = "<directory>", usage = "specify where to place generated class files")
  String destination = "./bin";

  @Option(name = "-cp", aliases = "-classpath", metaVar = "<path>", usage = "specify where to find user class files and annotation processors")
  String additionalClassPath = "";

  @Option(name = "-help", usage = "usage information")
  boolean usageFlag;

  @Option(name = "-version", usage = "version information")
  boolean versionFlag;

  @Option(name = "-debugmode", usage = "output messages about internal behavior of the compiler")
  boolean isDebugMode;

  @Option(name = "-toJava", usage = "generate Java codes instead of class files")
  boolean translateToJava;

  @Argument
  private List<String> args = new ArrayList<>();


  public boolean hasArgs() {
    return ! args.isEmpty();
  }

  public List<String> getArgs() {
    return args;
  }

  public boolean failedToParse () {
    return error.isPresent();
  }

  public void printFailMessage () {
    assert error.isPresent();
    System.err.println(error.get().getMessage());
    parser.printUsage(System.err);
  }

  public void printUsage () {
    System.out.println("Usage:");
    parser.printUsage(System.out);
  }

  public static CommandLineArgs parse(String[] argsArray) {
    CommandLineArgs args = new CommandLineArgs();
    args.parser = new CmdLineParser(args);
    args.parser.setUsageWidth(100);

    try {
      args.parser.parseArgument(argsArray);
      return args;
    } catch (CmdLineException e) {
      args.error = Optional.of(e);
      return args;
    }
  }

  private Optional<CmdLineException> error = Optional.empty();

  private CommandLineArgs() {}

  private CmdLineParser parser;
}
