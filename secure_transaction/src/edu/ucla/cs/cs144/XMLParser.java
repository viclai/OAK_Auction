package edu.ucla.cs.cs144;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

class XMLParser
{
  /* Non-recursive (NR) version of Node.getElementsByTagName(...)
   */
  static Element[] getElementsByTagNameNR(Element e, String tagName)
  {
    Vector< Element > elements = new Vector< Element >();
    Node child = e.getFirstChild();
    while (child != null)
    {
      if (child instanceof Element && child.getNodeName().equals(tagName))
      {
        elements.add( (Element)child );
      }
      child = child.getNextSibling();
    }
    Element[] result = new Element[elements.size()];
    elements.copyInto(result);
    return result;
  }

  /* Returns the first subelement of e matching the given tagName, or
   * null if one does not exist. NR means Non-Recursive.
   */
  static Element getElementByTagNameNR(Element e, String tagName)
  {
    Node child = e.getFirstChild();
    while (child != null)
    {
      if (child instanceof Element && child.getNodeName().equals(tagName))
        return (Element) child;
      child = child.getNextSibling();
    }
    return null;
  }

  /* Returns the text associated with the given element (which must have
   * type #PCDATA) as child, or "" if it contains no text.
   */
  static String getElementText(Element e)
  {
    if (e.getChildNodes().getLength() == 1)
    {
      Text elementText = (Text) e.getFirstChild();
      return elementText.getNodeValue();
    }
    else
      return "";
  }

  /* Returns the text (#PCDATA) associated with the first subelement X
   * of e with the given tagName. If no such X exists or X contains no
   * text, "" is returned. NR means Non-Recursive.
   */
  static String getElementTextByTagNameNR(Element e, String tagName)
  {
    Element elem = getElementByTagNameNR(e, tagName);
    if (elem != null)
      return getElementText(elem);
    else
      return "";
  }
}
