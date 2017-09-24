/* CS 144
 *
 * Implementation based on parser skeleton for processing item-???.xml files.
 * Must be compiled in JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 */

package edu.ucla.bol.victorlai;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser 
{
    
  static final String columnSeparator = "|@|";
  static DocumentBuilder builder;
    
  static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
  };
    
  static class MyErrorHandler implements ErrorHandler 
  {
        
    public void warning(SAXParseException exception) throws SAXException
    {
      fatalError(exception);
    }
        
    public void error(SAXParseException exception) throws SAXException
    {
      fatalError(exception);
    }
        
    public void fatalError(SAXParseException exception) throws SAXException
    {
      exception.printStackTrace();
      System.out.println("There should be no errors " +
                         "in the supplied XML files.");
      System.exit(3);
    }
        
  }
    
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
    
  /* Returns the amount (in XXXXX.xx format) denoted by a money-string
   * like $3,453.23. Returns the input if the input is an empty string.
   */
  static String strip(String money)
  {
    if (money.equals(""))
      return money;
    else
    {
      double am = 0.0;
      NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
      try
      {
        am = nf.parse(money).doubleValue();
      }
      catch (ParseException e)
      {
        System.out.println("This method should work for all " +
                           "money values you find in our data.");
        System.exit(20);
      }
      nf.setGroupingUsed(false);
      return nf.format(am).substring(1);
    }
  }
    
  /* Process one items-???.xml file.
   */
  static void processFile(File xmlFile)
  {
    Document doc = null;
    try
    {
      doc = builder.parse(xmlFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(3);
    }
    catch (SAXException e)
    {
      System.out.println("Parsing error on file " + xmlFile);
      System.out.println("  (not supposed to happen with supplied XML files)");
      e.printStackTrace();
      System.exit(3);
    }
        
    /* At this point 'doc' contains a DOM representation of an 'Items' XML
     * file. Use doc.getDocumentElement() to get the root Element. */
    System.out.println("Successfully parsed - " + xmlFile);
        
    /* Create load files and populate them with data from 'doc' */
    PrintWriter itemWriter = null;
    PrintWriter itemCategoryWriter = null;
    PrintWriter itemBuyPriceWriter = null;
    PrintWriter bidWriter = null;
    PrintWriter bidderRatingWriter = null;
    PrintWriter userLocationWriter = null;
    PrintWriter sellerRatingWriter = null;
    PrintWriter locationWriter = null;
    try
    {
      itemWriter = 
        new PrintWriter(new FileOutputStream(new File("item.del"), true));
      itemCategoryWriter = 
        new PrintWriter(new FileOutputStream(new File("item_category.del"),
                        true));
      itemBuyPriceWriter = 
        new PrintWriter(new FileOutputStream(new File("item_buy_price.del"),
                        true));
      bidWriter = 
        new PrintWriter(new FileOutputStream(new File("bid.del"), true));
      bidderRatingWriter = 
        new PrintWriter(new FileOutputStream(new File("bidder_rating.del"),
                        true));
      userLocationWriter = 
        new PrintWriter(new FileOutputStream(new File("user_location.del"),
                        true));
      sellerRatingWriter = 
        new PrintWriter(new FileOutputStream(new File("seller_rating.del"),
                        true));
      locationWriter = 
        new PrintWriter(new FileOutputStream(new File("item_location.del"),
                        true));
    }
    catch (IOException e)
    {
      System.out.println("File I/O error");
      System.exit(-1);
    }

    SimpleDateFormat oldFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Element root = doc.getDocumentElement();
    Element[] items = getElementsByTagNameNR(root, "Item");
    for (int i = 0; i < items.length; i++)
    {
      Element curItem = items[i];

      /* Item(ITEMID, Name, Currently, First_Bid, Number_of_Bids, Location,
       *      COuntry, Started, Ends, SellerID, Description) */
      String itemID = curItem.getAttribute("ItemID");

      Element nameElem = getElementByTagNameNR(curItem, "Name");
      String name = getElementText(nameElem);

      Element currentlyElem = getElementByTagNameNR(curItem, "Currently");
      String currently = getElementText(currentlyElem);
      currently = strip(currently);

      Element firstBidElem = getElementByTagNameNR(curItem, "First_Bid");
      String firstBid = getElementText(firstBidElem);
      firstBid = strip(firstBid);

      Element nBidsElem = getElementByTagNameNR(curItem, "Number_of_Bids");
      String nBids = getElementText(nBidsElem);

      Element locElem = getElementByTagNameNR(curItem, "Location");
      String location = getElementText(locElem);

      Element countryElem = getElementByTagNameNR(curItem, "Country");
      String country = getElementText(countryElem);
      
      Element startedElem = getElementByTagNameNR(curItem, "Started");
      String started = getElementText(startedElem);
      try
      {
        started = newFormat.format(oldFormat.parse(started));
      }
      catch (ParseException e)
      {
        System.out.println("Parse error");
        System.exit(1);
      }

      Element endsElem = getElementByTagNameNR(curItem, "Ends");
      String ends = getElementText(endsElem);
      try
      {
        ends = newFormat.format(oldFormat.parse(ends));
      }
      catch (ParseException e)
      {
        System.out.println("Parse error");
        System.exit(1);
      }

      Element sellerElem = getElementByTagNameNR(curItem, "Seller");
      String sellerID = sellerElem.getAttribute("UserID");

      Element descriptionElem = getElementByTagNameNR(curItem, "Description");
      String description = getElementText(descriptionElem);
      if (description.length() > 4000) // Truncate
        description = description.substring(0, 4000);

      String itemTuple = itemID + columnSeparator + name + columnSeparator +
                         currently + columnSeparator + firstBid +
                         columnSeparator + nBids + columnSeparator +
                         location + columnSeparator + country + 
                         columnSeparator + started + columnSeparator + ends +
                         columnSeparator + sellerID + columnSeparator +
                         description + "\n";
      itemWriter.append(itemTuple);
      itemWriter.flush();
      
      /* ItemCategory(ITEMID, CATEGORY) */
      Element[] categories = getElementsByTagNameNR(curItem, "Category");
      for (int j = 0; j < categories.length; j++)
      {
        Element curCategory = categories[j];
        String ctgry = getElementText(curCategory);
        String categoryTuple = itemID + columnSeparator + ctgry + "\n";
        itemCategoryWriter.append(categoryTuple);
        itemCategoryWriter.flush();
      }

      /* ItemBuyPrice(ITEMID, Buy_Price) */
      Element buyPriceElem = getElementByTagNameNR(curItem, "Buy_Price");
      if (buyPriceElem != null)
      {
        String buyPrice = getElementText(buyPriceElem);
        buyPrice = strip(buyPrice);
        String buyPriceTuple = itemID + columnSeparator + buyPrice + "\n";
        itemBuyPriceWriter.append(buyPriceTuple);
        itemBuyPriceWriter.flush();
      }

      Element bidsRoot = getElementByTagNameNR(curItem, "Bids");
      Element[] bids = getElementsByTagNameNR(bidsRoot, "Bid");
      for (int j = 0; j < bids.length; j++)
      {
        Element curBid = bids[j];

        /* Bid(BIDDERID, ITEMID, TIME, Amount) */
        Element bidderElem = getElementByTagNameNR(curBid, "Bidder");
        String bidderID = bidderElem.getAttribute("UserID");
        
        Element timeElem = getElementByTagNameNR(curBid, "Time");
        String time = getElementText(timeElem);
        try
        {
          time = newFormat.format(oldFormat.parse(time));
        }
        catch (ParseException e)
        {
          System.out.println("Parse error");
          System.exit(1);
        }

        Element amtElem = getElementByTagNameNR(curBid, "Amount");
        String amount = getElementText(amtElem);
        amount = strip(amount);

        String bidTuple = bidderID + columnSeparator + itemID +
                          columnSeparator + time + columnSeparator + amount +
                          "\n";
        bidWriter.append(bidTuple);
        bidWriter.flush();
        
        /* UserLocation(ID, Location, Country) */
        Element locationElem = getElementByTagNameNR(bidderElem, "Location");
        String userLoc = "\\N";
        if (locationElem != null)
          userLoc = getElementText(locationElem);

        Element countryUElem = getElementByTagNameNR(bidderElem, "Country");
        String countryU = "\\N";
        if (countryUElem != null)
          countryU = getElementText(countryUElem);

        String locTuple = bidderID + columnSeparator + userLoc +
                          columnSeparator + countryU + "\n";
        userLocationWriter.append(locTuple);
        userLocationWriter.flush();
        
        /* BidderRating(ID, Rating) */
        String bidderRating = bidderElem.getAttribute("Rating");
        String ratingTuple = bidderID + columnSeparator + bidderRating + "\n";
        bidderRatingWriter.append(ratingTuple);
        bidderRatingWriter.flush();
      }

      /* SellerRating(ID, Rating) */
      String sellerRating = sellerElem.getAttribute("Rating");
      String sellRatingTuple = sellerID + columnSeparator + sellerRating +
                               "\n";
      sellerRatingWriter.append(sellRatingTuple);
      sellerRatingWriter.flush();
      
      /* Location(ITEMID, latitude, longitude) */
      String latitude = locElem.getAttribute("Latitude");
      if (latitude == "")
        latitude = "\\N";
      String longitude = locElem.getAttribute("Longitude");
      if (longitude == "")
        longitude = "\\N";
      
      if (latitude != "\\N" || longitude != "\\N")
      {
        String latlongTuple = itemID + columnSeparator + latitude +
                              columnSeparator + longitude + "\n";
        locationWriter.append(latlongTuple);
        locationWriter.flush();
      }
    }

    // Flush and close all FileWriter instances
    locationWriter.close();

    sellerRatingWriter.close();

    userLocationWriter.close();

    bidderRatingWriter.close();

    bidWriter.close();

    itemBuyPriceWriter.close();

    itemCategoryWriter.close();
    
    itemWriter.close();
    /**************************************************************/
        
  }
    
  public static void main (String[] args)
  {
    if (args.length == 0)
    {
      System.out.println("Usage: java MyParser [file] [file] ...");
      System.exit(1);
    }
        
    /* Initialize parser. */
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringElementContentWhitespace(true);      
      builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new MyErrorHandler());
    }
    catch (FactoryConfigurationError e)
    {
      System.out.println("unable to get a document builder factory");
      System.exit(2);
    } 
    catch (ParserConfigurationException e)
    {
      System.out.println("parser was unable to be configured");
      System.exit(2);
    }
        
    /* Process all files listed on command line. */
    for (int i = 0; i < args.length; i++)
    {
      File currentFile = new File(args[i]);
      processFile(currentFile);
    }
  }
}
