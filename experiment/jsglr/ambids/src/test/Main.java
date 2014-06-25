package test;

public class Main {
  public static void main(String[] args) throws Exception {
    if(args.length != 4) System.out.println("# of args should be 4");
    int size = Integer.parseInt(args[0]);
    int len = Integer.parseInt(args[1]);
    int num  = Integer.parseInt(args[2]);
    String file = "/home/ichikawa/Documents/research/experiment/jsglr/ambids/grammar/Syntax" + args[3] + ".tbl";
    
    String str = "a";
    for (int i = 1; i < size; i++) str = str + "a";

    String dotStr = str;
    for (int i = 1; i < len; i++) dotStr = dotStr + "." + str;
    
    StringBuilder buf = new StringBuilder();

    buf.append("{");
    for (int i = 0; i < num; i++) buf.append(dotStr).append(" ;\n");
    buf.append("}");

    System.out.println("---------------");
    System.out.println("program length = " + num);
    System.out.println("syntax file = " + file);
    
    tryParse(buf.toString(), file);
  }
  
  private static void tryParse(String s, String file) throws Exception {
    Parser p = new Parser(file);
    
    long start = System.currentTimeMillis();
    
    p.parse(s);

    long end = System.currentTimeMillis();
    
    //long memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    
    System.out.println("parse time = " + (end - start) + " msec.");
    //System.out.println("memory = " + memory + " MB");
    System.out.println("ambiguities = " + p.amb());
  }
}
