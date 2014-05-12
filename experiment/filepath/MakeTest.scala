
import java.io.PrintWriter

def make(i: Int, a: String): String = {
  "import file.*;\n" +
  "using file.FilePathOperators;\n\n" +
  "public class Test" + i + " {\n" +
  "  public static void main(String[] args) {\n" +
  "    FilePath path = " + a + ";\n" +
  "    System.out.println(path.getAbsolutePath());\n" +
  "  }\n" +
  "}"
}

var a = "a"

for (i <- 1 to 1000) {
  println("make " + i)

  val out = new PrintWriter("./src/Test" + i + ".pj")
  out.println(make(i, a))
  out.close

  a = a + "/a"
}

