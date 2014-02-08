package proteaj;

import proteaj.error.*;
import proteaj.ast.*;
import proteaj.io.*;
import proteaj.ir.*;

import java.util.*;
import java.io.File;

public class SigCompiler {
  public IR compile(Collection<File> files) {
    List<CompilationUnit> cunits = new ArrayList<CompilationUnit>();

    for(File file : files) try {
      SourceFileReader reader = new SourceFileReader(file);
      SigLexer lexer = new SigLexer(reader);
      SigParser parser = new SigParser(lexer);

      CompilationUnit cunit = parser.parseCompilationUnit();

      SigSemanticsChecker checker = new SigSemanticsChecker(cunit);
      if(checker.checkAll()) cunits.add(cunit);
    } catch (FileIOError e) {
      ErrorList.addError(e);
    } catch (ParseError e) {
      ErrorList.addError(e);
    }

    SigIRGenerator irgen = new SigIRGenerator(cunits);
    return irgen.generateIR();
  }
}
