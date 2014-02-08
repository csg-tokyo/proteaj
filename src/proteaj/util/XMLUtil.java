package proteaj.util;

import static org.w3c.dom.Node.ELEMENT_NODE;

import org.w3c.dom.Node;

import proteaj.error.FileIOError;

public class XMLUtil {
  public static boolean hasAttr(Node node, String name) {
    return node.getAttributes().getNamedItem(name) != null;
  }

  public static String getAttr(Node node, String name) {
    return node.getAttributes().getNamedItem(name).getNodeValue();
  }

  public static boolean checkElementNode(Node node, String name) throws FileIOError {
    if(! checkNodeType(node, ELEMENT_NODE)) return false;
    if(! node.getNodeName().equals(name)) return false;
    return true;
  }

  public static boolean checkNodeType(Node node, short type) throws FileIOError {
    if(node.getNodeType() != type) return false;
    return true;
  }
}

