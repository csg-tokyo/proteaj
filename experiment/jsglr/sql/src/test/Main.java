package test;

public class Main {
  public static void main(String[] args) throws Exception {
    if(args.length != 3) System.out.println("# of args should be 3");
    int size = Integer.parseInt(args[0]);
    int len = Integer.parseInt(args[1]);
    int num  = Integer.parseInt(args[2]);
    
    String str = "a";
    for (int i = 1; i < size; i++) str = str + "a";

    String dotStr = str;
    for (int i = 1; i < len; i++) dotStr = dotStr + '+' + str;
    
    StringBuilder buf = new StringBuilder();

    buf.append("{");
    for (int i = 0; i < num; i++) buf.append(dotStr).append(" ;\n");
    buf.append("}");

    System.out.println("---------------");
    System.out.println("string length = " + size);
    System.out.println("dot chain length = " + len);
    System.out.println("program length = " + num);
    
    tryParse(buf.toString());
  }
  
  private static void tryParse(String s) throws Exception {
    Parser p = new Parser("/home/ichikawa/Documents/research/experiment/jsglr/sql/grammar/Syntax.tbl");
    
    long start = System.currentTimeMillis();
    
    p.parse(s);

    long end = System.currentTimeMillis();
    
    //long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    
    System.out.println("parse time = " + (end - start) + " msec.");
    //System.out.println("memory = " + memory + " MB");
    System.out.println("ambiguities = " + p.amb());
  }
}
