package proteaj.io;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javassist.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import proteaj.error.*;
import proteaj.ir.*;

import static org.w3c.dom.Node.*;
import static proteaj.ir.IRPattern.*;
import static proteaj.util.XMLUtil.getAttr;
import static proteaj.util.XMLUtil.hasAttr;

public class OperatorsFile {
  public OperatorsFile(IRSyntax irsyn) {
    name = irsyn.getName();

    try {
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
      doc = docbuilder.newDocument();

      Element root = doc.createElement("operators");
      root.setAttribute("name", name);
      if(irsyn.hasBaseSyntax()) root.setAttribute("super", irsyn.getBaseSyntax());

      List<String> mixins = irsyn.getMixinSyntax();
      for(int i = 0; i < mixins.size(); i++) {
        Element mixin = doc.createElement("mixin");
        mixin.setAttribute("id", String.valueOf(i));
        mixin.setAttribute("operators", mixins.get(i));
        root.appendChild(mixin);
      }

      doc.appendChild(root);

      List<IROperator> irops = irsyn.getOperators();
      for(int i = 0; i < irops.size(); i++) {
        IROperator op = irops.get(i);
        Element operator = doc.createElement("operator");
        operator.setAttribute("id", String.valueOf(i));
        operator.setAttribute("return", op.getReturnType().getName());
        operator.setAttribute("method", op.getMethodName());
        operator.setAttribute("priority", String.valueOf(op.getPriority()));
        root.appendChild(operator);

        operator.appendChild(toXML(op.getPattern()));
      }

    } catch (ParserConfigurationException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private OperatorsFile(String name, File file) throws FileIOError {
    this.name = name;
    if(file.isFile() && file.canRead()) try {
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
      doc = docbuilder.parse(file);
    } catch (ParserConfigurationException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new FileIOError(file.getName() + " is broken", file.getName(), 0);
    } catch (IOException e) {
      throw new FileIOError("can't read file", file.getName(), 0);
    }
  }

  public static OperatorsFile loadOperatorsFile(String name) throws FileIOError {
    String fileName = getOpsFileName(name);
    URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
    if(url != null) try {
      File file = new File(url.toURI());
      return new OperatorsFile(name, file);
    } catch (URISyntaxException e) {
      ErrorList.addError(new FileIOError(fileName + " is broken", fileName, 0));
    }

    return null;
  }

  public void write() throws CompileError {
    write(".");
  }

  public void write(String target) throws CompileError {
    try {
      TransformerFactory tfactory = TransformerFactory.newInstance();
      Transformer transformer = tfactory.newTransformer();

      File file = new File(ops2path(target, name));
      transformer.transform(new DOMSource(doc), new StreamResult(file));
    } catch (TransformerConfigurationException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (TransformerException e) {
      String fileName = getOpsFileName(name);
      throw new FileIOError("can't write file", fileName, 0);
    }
  }

  public IRSyntax read(ClassPool cpool) throws FileIOError {
    CtClass clz = cpool.getOrNull(name);
    String fileName = getOpsFileName(name);

    if(clz == null) return null;

    Element root = doc.getDocumentElement();
    checkElementNode(root, "operators");
    if(! getAttr(root, "name").equals(name)) throw new FileIOError(fileName + " is broken", fileName, 0);

    IRSyntax irsyn = new IRSyntax(name);

    if(hasAttr(root, "super")) {
      irsyn.setBaseSyntax(getAttr(root, "super"));
    }

    NodeList mixinlist = root.getElementsByTagName("mixin");
    String[] mixinops  = new String[mixinlist.getLength()];
    for(int i = 0; i < mixinlist.getLength(); i++) {
      Node node = mixinlist.item(i);
      checkElementNode(node, "mixin");

      int id = Integer.parseInt(getAttr(node, "id"));
      if(id < 0 || id >= mixinlist.getLength()) throw new FileIOError(fileName + " is broken", fileName, 0);

      mixinops[id] = getAttr(node, "operators");
    }

    for(String mixin : mixinops) {
      if(mixin == null) throw new FileIOError(fileName + " is broken", fileName, 0);
      irsyn.addMixinSyntax(mixin);
    }

    NodeList nlist = root.getElementsByTagName("operator");
    IROperator[] irops = new IROperator[nlist.getLength()];

    for(int i = 0; i < nlist.getLength(); i++) try {
      Node node = nlist.item(i);
      checkElementNode(node, "operator");

      int id = Integer.parseInt(getAttr(node, "id"));
      if(id < 0 || id >= nlist.getLength()) throw new FileIOError(fileName + " is broken", fileName, 0);

      String methodName = getAttr(node, "method");
      int priority = Integer.parseInt(getAttr(node, "priority"));
      CtClass returnType = cpool.get(getAttr(node, "return"));

      Node ptnode = node.getFirstChild();
      checkElementNode(ptnode, "pattern");

      IRPattern irpat = fromXML(ptnode, clz, cpool, fileName);

      CtMethod method = clz.getDeclaredMethod(methodName);

      IROperator irop = new IROperator(returnType, irpat, priority, clz, method);
      irops[id] = irop;

    } catch (NotFoundException e) {
      return null;
    } catch (NotFoundError e) {
      return null;
    } catch (FileIOError e) {
      ErrorList.addError(e);
    }

    for(IROperator irop : irops) {
      if(irop == null) throw new FileIOError(fileName + " is broken", fileName, 0);
      irsyn.addOperator(irop);
    }

    return irsyn;
  }

  public IRPattern fromXML(Node ptnode, CtClass clz, ClassPool cpool, String fileName) throws FileIOError, NotFoundError {
    int modifier = Integer.parseInt(getAttr(ptnode, "modifier"));
    int patlength = Integer.parseInt(getAttr(ptnode, "length"));
    int operatorsLength = Integer.parseInt(getAttr(ptnode, "operators"));
    int operandsLength = Integer.parseInt(getAttr(ptnode, "operands"));
    int andPredsLength = Integer.parseInt(getAttr(ptnode, "andpreds"));
    int notPredsLength = Integer.parseInt(getAttr(ptnode, "notpreds"));

    NodeList ptlist = ptnode.getChildNodes();
    if(ptlist.getLength() != patlength) throw new FileIOError(fileName + " is broken", fileName, 0);

    int[] patternIds = new int[patlength];
    CtClass[] operandTypes = new CtClass[operandsLength];
    IROperandAttribute[] operandMods = new IROperandAttribute[operandsLength];
    String[] keywords = new String[operatorsLength];
    CtClass[] andpreds = new CtClass[andPredsLength];
    CtClass[] notpreds = new CtClass[notPredsLength];

    for(int j = 0; j < patlength; j++) try {
      Node elem = ptlist.item(j);
      checkNodeType(elem, ELEMENT_NODE);

      int index = Integer.parseInt(getAttr(elem, "index"));
      int id = Integer.parseInt(getAttr(elem, "id"));

      patternIds[index] = id;

      if(elem.getNodeName().equals("keyword")) {
        id &= ~PATTERN_OPERATOR;
        String keyword = getAttr(elem, "name");
        keywords[id] = keyword;
      }
      else if(elem.getNodeName().equals("operand")) {
        id &= ~PATTERN_OPERAND;

        CtClass type = cpool.get(getAttr(elem, "type"));
        operandTypes[id] = type;

        int mod = Integer.parseInt(getAttr(elem, "modifier"));

        if(hasAttr(elem, "separator")) {
          operandMods[id] = new IROperandAttribute(mod, getAttr(elem, "separator"));
        }
        else {
          operandMods[id] = new IROperandAttribute(mod);
        }

        if(hasAttr(elem, "default")) {
          operandMods[id].setDefaultMethod(clz.getDeclaredMethod(getAttr(elem, "default")));
        }
      }
      else if(elem.getNodeName().equals("and")) {
        id &= ~PATTERN_ANDPRED;
        CtClass type = cpool.get(getAttr(elem, "type"));
        andpreds[id] = type;
      }
      else if(elem.getNodeName().equals("not")) {
        id &= ~PATTERN_NOTPRED;
        CtClass type = cpool.get(getAttr(elem, "type"));
        notpreds[id] = type;
      }
    } catch (NotFoundException e) {
      throw new NotFoundError(e, fileName, 0);
    }

    return new IRPattern(modifier, patternIds, operandTypes, operandMods, keywords, andpreds, notpreds);
  }

  public Element toXML(IRPattern p) {
    int patlength = p.getPatternLength();
    Element pattern = doc.createElement("pattern");
    pattern.setAttribute("modifier", String.valueOf(p.getModifier()));
    pattern.setAttribute("length", String.valueOf(patlength));
    pattern.setAttribute("operators", String.valueOf(p.getOperatorsLength()));
    pattern.setAttribute("operands", String.valueOf(p.getOperandsLength()));
    pattern.setAttribute("andpreds", String.valueOf(p.getAndPredicatesLength()));
    pattern.setAttribute("notpreds", String.valueOf(p.getNotPredicatesLength()));

    for(int i = 0; i < patlength; i++) {
      if(p.isOperator(i)) {
        Element keyword = doc.createElement("keyword");
        keyword.setAttribute("index", String.valueOf(i));
        keyword.setAttribute("id", String.valueOf(p.getPatternId(i)));
        keyword.setAttribute("name", p.getOperatorKeyword(i));
        pattern.appendChild(keyword);
      }
      else if(p.isOperand(i)) {
        Element operand = doc.createElement("operand");
        operand.setAttribute("index", String.valueOf(i));
        operand.setAttribute("id", String.valueOf(p.getPatternId(i)));
        operand.setAttribute("type", p.getOperandType(i).getName());
        operand.setAttribute("modifier", String.valueOf(p.getOperandModifier(i).getModifier()));
        if(p.getOperandModifier(i).hasSeparator()) {
          operand.setAttribute("separator", p.getOperandModifier(i).getSeparator());
        }
        if(p.isOptionOperand(i)) {
          operand.setAttribute("default", p.getDefaultMethod(i).getName());
        }
        pattern.appendChild(operand);
      }
      else if(p.isAndPredicate(i)) {
        Element pred = doc.createElement("and");
        pred.setAttribute("index", String.valueOf(i));
        pred.setAttribute("id", String.valueOf(p.getPatternId(i)));
        pred.setAttribute("type", p.getAndPredicateType(i).getName());
        pattern.appendChild(pred);
      }
      else if(p.isNotPredicate(i)) {
        Element pred = doc.createElement("not");
        pred.setAttribute("index", String.valueOf(i));
        pred.setAttribute("id", String.valueOf(p.getPatternId(i)));
        pred.setAttribute("type", p.getNotPredicateType(i).getName());
        pattern.appendChild(pred);
      }
    }

    return pattern;
  }

  private void checkElementNode(Node node, String name) throws FileIOError {
    checkNodeType(node, ELEMENT_NODE);
    if(! node.getNodeName().equals(name)) {
      String fileName = getOpsFileName(name);
      throw new FileIOError(fileName + " is broken", fileName, 0);
    }
  }

  private void checkNodeType(Node node, short type) throws FileIOError {
    if(node.getNodeType() != type) {
      String fileName = getOpsFileName(name);
      throw new FileIOError(fileName + " is broken", fileName, 0);
    }
  }

  public static String getOpsFileName(String name) {
    return name.replace('.', '/') + ".ops";
  }

  public static String ops2path(String directory, String name) {
    if(directory.endsWith("/")) return directory + name.replace('.', '/') + ".ops";
    else return directory + '/' + name.replace('.', '/') + ".ops";
  }

  private Document doc;
  private String name;
}
