package test;

public class Main {
  public static void main(String[] args) throws Exception {
    if(args.length == 0) System.out.println("missing argument: integer number > 0");
    int size = Integer.parseInt(args[0]);
    String str = "a";
    for (int i = 1; i < size; i++) str = str + "/a";
    
    System.out.println("---------------");
    System.out.println("length = " + size);
    
    tryParse(str);
  }
  
  private static void tryParse(String s) throws Exception {
    Parser p = new Parser("/home/ichikawa/Documents/research/experiment/jsglr/filepath/grammar/ExprSyntax.tbl");
   //    Parser p = new Parser("/home/ichikawa/workspace/ExprSyntax/include/ExprSyntax.tbl");
    
    long start = System.currentTimeMillis();
    
    p.parse(s);

    long end = System.currentTimeMillis();
    
    //long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    
    System.out.println("parse time = " + (end - start) + " msec.");
    //System.out.println("memory = " + memory + " MB");
    System.out.println("ambiguities = " + p.amb());
  }
}
