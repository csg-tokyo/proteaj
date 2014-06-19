package test;

public class Main {
  public static void main(String[] args) throws Exception {
      System.out.println("press any key to continue...");
      System.in.read();
    // example ::
    // { grep a.a a/a/a/a; }

    StringBuilder buf = new StringBuilder();

    buf.append("{\n");
    buf.append("  grep");
    for (int i = 0; i < args.length; i++) buf.append(' ').append(args[i]);
    buf.append(";\n");
    buf.append("}");

    tryParse(buf.toString());
  }
  
  private static void tryParse(String s) throws Exception {
    Parser p = new Parser("/home/ichikawa/Documents/research/experiment/jsglr/grep/grammar/Syntax.tbl");
    
    long start = System.currentTimeMillis();
    
    p.parse(s);

    long end = System.currentTimeMillis();
    
    //long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    
    System.out.println("parse time = " + (end - start) + " msec.");
    //System.out.println("memory = " + memory + " MB");
    System.out.println("ambiguities = " + p.amb());
  }
}
