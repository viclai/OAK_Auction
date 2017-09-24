package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.xml.sax.InputSource;

public class ItemServlet extends HttpServlet implements Servlet
{
  public ItemServlet() {}

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException
  {
    String itemId = request.getParameter("id");
    if (itemId == null || itemId.equals(""))
    {
      request.setAttribute("invalid", "Please enter an item ID.");
      request.getRequestDispatcher("/item.jsp").forward(request, response);
      return;
    }
    request.setAttribute("itemId", itemId);

    String xml = AuctionSearchClient.getXMLDataForItemId(itemId);
    if (xml.equals(""))
    {
      request.setAttribute("invalid",
                           "Item ID " + itemId + " does not exist.");
      request.getRequestDispatcher("/item.jsp").forward(request, response);
      return;
    }

    // Convert string to XML Document object
    Document doc = null;
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(new InputSource(new StringReader(xml)));
    }
    catch (FactoryConfigurationError e)
    {
      System.err.println("Unable to get a document builder factory");
      e.printStackTrace();
    }
    catch (ParserConfigurationException e)
    {
      System.out.println("Parser was unable to be configured");
      e.printStackTrace();
    }
    catch (SAXException e)
    {
      System.out.println("Parsing error on XML document");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    Element item = doc.getDocumentElement();

    Element nameElem = XMLParser.getElementByTagNameNR(item, "Name");
    String name = XMLParser.getElementText(nameElem);
    request.setAttribute("name", name);

    Element[] categoryElems =
      XMLParser.getElementsByTagNameNR(item, "Category");
    ArrayList<String> ctgryList = new ArrayList<String>();
    for (int i = 0; i < categoryElems.length; i++)
    {
      Element ctgyElem = categoryElems[i];
      String category = XMLParser.getElementText(ctgyElem);
      ctgryList.add(category);
    }
    String[] categories = new String[ctgryList.size()];
    categories = ctgryList.toArray(categories);
    request.setAttribute("categories", categories);

    Element currentlyElem =
      XMLParser.getElementByTagNameNR(item, "Currently");
    String currently = XMLParser.getElementText(currentlyElem);
    request.setAttribute("currently", currently);

    Element firstBidElem = XMLParser.getElementByTagNameNR(item, "First_Bid");
    String firstBid;
    if (firstBidElem == null)
      firstBid = "N.A.";
    else
      firstBid = XMLParser.getElementText(firstBidElem);
    request.setAttribute("firstBid", firstBid);

    Element buyPriceElem =
      XMLParser.getElementByTagNameNR(item, "Buy_Price");
    String buyPrice = "";
    if (buyPriceElem != null)
    {
      buyPrice = XMLParser.getElementText(buyPriceElem);
      request.setAttribute("buyPrice", buyPrice);
    }

    Element nBidsElem =
      XMLParser.getElementByTagNameNR(item, "Number_of_Bids");
    String nBids = XMLParser.getElementText(nBidsElem);
    request.setAttribute("nBids", nBids);

    Element bidsRoot = XMLParser.getElementByTagNameNR(item, "Bids");
    Element[] bidsElems = XMLParser.getElementsByTagNameNR(bidsRoot, "Bid");
    TreeMap<Long, Bid> bidMap = new TreeMap<Long, Bid>(); // Sort bids by date
    SimpleDateFormat curFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
    SimpleDateFormat userFormat =
        new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");
    SimpleDateFormat sortFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    for (int i = 0; i < bidsElems.length; i++)
    {
      Element curBid = bidsElems[i];

      Element bidderElem = XMLParser.getElementByTagNameNR(curBid, "Bidder");
      String bidderID = bidderElem.getAttribute("UserID");

      Element timeElem = XMLParser.getElementByTagNameNR(curBid, "Time");
      String time = XMLParser.getElementText(timeElem);
      
      String sortedDateStr = "";
      try
      {
        sortedDateStr = sortFormat.format(curFormat.parse(time));
      }
      catch (ParseException e)
      {
        System.err.println("Parse error");
        e.printStackTrace();
      }

      Long index = null;
      try
      {
        index = Long.parseLong(sortedDateStr);
      }
      catch (NumberFormatException e)
      {
        System.err.println("Long conversion error");
        e.printStackTrace();
      }

      try
      {
        time = userFormat.format(curFormat.parse(time));
      }
      catch (ParseException e)
      {
        System.err.println("Parse error");
        e.printStackTrace();
      }

      Element amtElem = XMLParser.getElementByTagNameNR(curBid, "Amount");
      String amount = XMLParser.getElementText(amtElem);

      Element locationElem =
        XMLParser.getElementByTagNameNR(bidderElem, "Location");
      String userLoc = "";
      if (locationElem != null)
        userLoc = XMLParser.getElementText(locationElem);

      Element countryUElem =
        XMLParser.getElementByTagNameNR(bidderElem, "Country");
      String countryU = "";
      if (countryUElem != null)
        countryU = XMLParser.getElementText(countryUElem);

      String bidderRating = bidderElem.getAttribute("Rating");

      Bid bid = new Bid(
        bidderID, bidderRating, userLoc, countryU, time, amount
      );
      bidMap.put(index, bid);
    }
    Bid[] bids = new Bid[bidMap.size()];
    Set<Long> keys = bidMap.keySet();
    int i = bids.length - 1;
    for (Long key : keys)
    {
      Bid bid = (Bid)bidMap.get(key);
      bids[i] = bid;
      i--;
    }
    request.setAttribute("bids", bids);

    Element locElem = XMLParser.getElementByTagNameNR(item, "Location");
    String location = XMLParser.getElementText(locElem);
    request.setAttribute("location", location);
    String latitude = locElem.getAttribute("Latitude");
    request.setAttribute("latitude", latitude);
    String longitude = locElem.getAttribute("Longitude");
    request.setAttribute("longitude", longitude);

    Element countryElem = XMLParser.getElementByTagNameNR(item, "Country");
    String country = XMLParser.getElementText(countryElem);
    request.setAttribute("country", country);

    Element startedElem = XMLParser.getElementByTagNameNR(item, "Started");
    String started = XMLParser.getElementText(startedElem);
    try
    {
      started = userFormat.format(curFormat.parse(started));
    }
    catch (ParseException e)
    {
      System.err.println("Parse error");
      e.printStackTrace();
    }
    request.setAttribute("started", started);

    Element endsElem = XMLParser.getElementByTagNameNR(item, "Ends");
    String ends = XMLParser.getElementText(endsElem);
    try
    {
      ends = userFormat.format(curFormat.parse(ends));
    }
    catch (ParseException e)
    {
      System.err.println("Parse error");
      e.printStackTrace();
    }
    request.setAttribute("ends", ends);

    Element sellerElem = XMLParser.getElementByTagNameNR(item, "Seller");
    String sellerID = sellerElem.getAttribute("UserID");
    request.setAttribute("sellerId", sellerID);
    String sellerRating = sellerElem.getAttribute("Rating");
    request.setAttribute("sellerRating", sellerRating);

    Element descriptionElem =
      XMLParser.getElementByTagNameNR(item, "Description");
    String description = XMLParser.getElementText(descriptionElem);
    request.setAttribute("description", description);

    HttpSession session = request.getSession(true);
    session.setAttribute("itemId", itemId);
    session.setAttribute("name", name);
    session.setAttribute("buyPrice", buyPrice);

    request.getRequestDispatcher("/item.jsp").forward(request, response);
  }
}
