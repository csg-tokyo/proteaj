package test;

import java.io.IOException;

import org.spoofax.jsglr.io.*;
import org.spoofax.jsglr.client.Asfix2TreeBuilder;
import org.spoofax.jsglr.client.InvalidParseTableException;
import org.spoofax.jsglr.client.SGLR;

public class Parser {
  
  public Parser(String file) throws IOException, InvalidParseTableException {
    sglr = new SGLR(new Asfix2TreeBuilder(), manager.loadFromFile(file));
  }
  
  public Object parse(String str) throws Exception {
    return sglr.parse(str, "file", "Start");
  }
  
  public int amb() {
    return sglr.getDisambiguator().getAmbiguityCount();
  }
  
  private SGLR sglr;
  private static final ParseTableManager manager = new ParseTableManager();
}
