package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch
{
  private IndexSearcher searcher = null;
  private QueryParser parser = null;

  public AuctionSearch()
  {
    try
    {
      searcher = new IndexSearcher(DirectoryReader.open(
        FSDirectory.open(new File("/var/lib/lucene/")))
      );
      parser = new QueryParser("content", new StandardAnalyzer());
    }
    catch (IOException ex)
    {
      System.err.println("IOException: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private Document getDocument(int docId) throws IOException
  {
    return searcher.doc(docId);
  }

  private ScoreDoc[] performSearch(Query query, int n) throws IOException
  {
    TopDocs topDocs = searcher.search(query, n);
    return topDocs.scoreDocs;
  }

  private String xmlEscapeStr(String s)
  {
    String escapedXml = "";
    for (int i = 0; i < s.length(); i++)
    {
      if (s.charAt(i) == '\"')
        escapedXml += "&quot;";
      else if (s.charAt(i) == '\'')
        escapedXml += "&apos;";
      else if (s.charAt(i) == '<')
        escapedXml += "&lt;";
      else if (s.charAt(i) == '>')
        escapedXml += "&gt;";
      else if (s.charAt(i) == '&')
        escapedXml += "&amp;";
      else
        escapedXml += s.charAt(i);
    }
    return escapedXml;
  }

  public SearchResult[] basicSearch(String query, int numResultsToSkip, 
                                    int numResultsToReturn)
  {
    int numRawResults = numResultsToSkip + numResultsToReturn;
    ArrayList<SearchResult> results = new ArrayList<SearchResult>();
    Query q = null;

    try
    {
      q = parser.parse(query);
    }
    catch (ParseException ex)
    {
      System.err.println("ParseException: " + ex.getMessage());
      ex.printStackTrace();
    }

    try
    {
      ScoreDoc[] hits = performSearch(q, numRawResults);
      for (int i = numResultsToSkip; i < hits.length; i++)
      {
        Document doc = getDocument(hits[i].doc);
        String id = doc.get("ItemID");
        String name = doc.get("Name");
        results.add(new SearchResult(id, name));
      }
    }
    catch (IOException ex)
    {
      System.err.println("IOException: " + ex.getMessage());
      ex.printStackTrace();
    }
    SearchResult[] resArr = new SearchResult[results.size()];
    return results.toArray(resArr);
  }

  public SearchResult[] spatialSearch(String query, SearchRegion region,
                                      int numResultsToSkip,
                                      int numResultsToReturn)
  {
    ArrayList<SearchResult> spatialResults = new ArrayList<SearchResult>();

    // Create a connection to the database
    Connection conn = null;
    try
    {
      conn = DbManager.getConnection(true);
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (Connecting to database): " +
                         ex.getMessage());
      ex.printStackTrace();
    }

    /* Get the total number of items */
    int totalItems = 0;
    try
    {
      PreparedStatement countStmt = conn.prepareStatement(
        "SELECT COUNT(*) AS count FROM Item"
      );
      ResultSet countRes = countStmt.executeQuery();
      countRes.next(); // This should not return false
      totalItems = countRes.getInt("count");
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (Counting items): " + ex.getMessage());
      ex.printStackTrace();
    }

    Query q = null;
    try
    {
      q = parser.parse(query);
    }
    catch (ParseException ex)
    {
      System.err.println("ParseException: " + ex.getMessage());
      ex.printStackTrace();
    }

    try
    {
      ScoreDoc[] hits = performSearch(q, totalItems);

      /* Get items that are spatially within the region specified */
      String spatialDim = region.getLx() + " " + region.getLy() + ", " +
                          region.getLx() + " " + region.getRy() + ", " +
                          region.getRx() + " " + region.getRy() + ", " +
                          region.getRx() + " " + region.getLy() + ", " +
                          region.getLx() + " " + region.getLy();

      HashSet<String> inRect = new HashSet<String>();
      PreparedStatement spatialStmt = conn.prepareStatement(
        "SELECT ItemID " +
        "FROM SpatialTable " +
        "WHERE MBRCONTAINS(GeomFromText(" +
                             "'Polygon((" + spatialDim + "))'" +
                           "), Location)"
      );
      ResultSet spatialRes = spatialStmt.executeQuery();
      while (spatialRes.next())
      {
        String id = spatialRes.getString("ItemID");
        inRect.add(id);
      }

      /* Place search results into an array to return */
      int skip = 0;
      for (int i = 0;
           i < hits.length && spatialResults.size() < numResultsToReturn;
           i++)
      {
        Document doc = getDocument(hits[i].doc);
        String id = doc.get("ItemID");
        if (inRect.contains(id))
        {
          if (skip < numResultsToSkip)
            skip++;
          else
          {
            String name = doc.get("Name");
            spatialResults.add(new SearchResult(id, name));
          }
        }
      }
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException: " + ex.getMessage());
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      System.err.println("IOException: " + ex.getMessage());
      ex.printStackTrace();
    }

    // Close the database connection
    try
    {
      conn.close();
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (Closing database connection): " +
                         ex.getMessage());
      ex.printStackTrace();
    }

    SearchResult[] resArr = new SearchResult[spatialResults.size()];
    return spatialResults.toArray(resArr);
  }

  public String getXMLDataForItemId(String itemId)
  {
    String xml = "";

    /* Create a connection to the database to retrieve the information
       associated with 'itemid' (if it exists) */
    Connection conn = null;
    try
    {
      conn = DbManager.getConnection(true);
	}
    catch (SQLException ex)
    {
      System.out.println("SQLException: " + ex.getMessage());
      ex.printStackTrace();
    }

    /* Get item information from Item table */
    ResultSet itemInfo = null;
    try
    {
      PreparedStatement itemStmt = conn.prepareStatement(
        "SELECT * FROM Item WHERE ItemID = ?"
      );
      itemStmt.setString(1, itemId);
      itemInfo = itemStmt.executeQuery();
      if (itemInfo.next() == false)
        return xml;
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (Item): " + ex.getMessage());
      ex.printStackTrace();
    }

    /* Get item information from ItemCategory table */
    ResultSet itemCtgyInfo = null;
    try
    {
      PreparedStatement itemCtgyStmt = conn.prepareStatement(
        "SELECT Category FROM ItemCategory WHERE ItemID = ?"
      );
      itemCtgyStmt.setString(1, itemId);
      itemCtgyInfo = itemCtgyStmt.executeQuery();
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (ItemCategory): " + ex.getMessage());
      ex.printStackTrace();
    }

    /* Get item information from ItemBuyPrice table */
    ResultSet itemPriceInfo = null;
    try
    {
      PreparedStatement itemPriceStmt = conn.prepareStatement(
        "SELECT Buy_Price FROM ItemBuyPrice WHERE ItemID = ?"
      );
      itemPriceStmt.setString(1, itemId);
      itemPriceInfo = itemPriceStmt.executeQuery();
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (ItemBuyPrice): " + ex.getMessage());
      ex.printStackTrace();
    }

    /* Get item information from Bid table */
    ResultSet bidInfo = null;
    try
    {
      PreparedStatement bidStmt = conn.prepareStatement(
        "SELECT BidderID, Time, Amount FROM Bid WHERE ItemID = ?"
      );
      bidStmt.setString(1, itemId);
      bidInfo = bidStmt.executeQuery();
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (Bid): " + ex.getMessage());
      ex.printStackTrace();
    }

    /* Get seller information from SellerRating table */
    ResultSet sellerInfo = null;
    try
    {
      PreparedStatement sellerStmt = conn.prepareStatement(
        "SELECT Rating FROM SellerRating WHERE ID = ?"
      );
      sellerStmt.setString(1, itemInfo.getString("SellerID"));
      sellerInfo = sellerStmt.executeQuery();
    }
    catch (SQLException ex)
    {
      System.out.println("SQLException (SellerRating): " + ex.getMessage());
      ex.printStackTrace();
    }
    
    /* Generate XML text */
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat xmlFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");

    try
    {
      String itemStartTag = "<Item ItemID=\"" + itemId + "\">";

      String nameTag = "<Name>" + xmlEscapeStr(itemInfo.getString("Name")) + 
                       "</Name>";

      String categoriesTags = "";
      while (itemCtgyInfo.next())
      {
        categoriesTags += "<Category>" +
                          xmlEscapeStr(itemCtgyInfo.getString("Category")) +
                          "</Category>";
      }

      String currentlyTag = "<Currently>$" +
                            xmlEscapeStr(itemInfo.getString("Currently")) +
                            "</Currently>";

      String buyPriceTag = "";
      if (itemPriceInfo.next())
      {
        buyPriceTag += "<Buy_Price>$" +
                       xmlEscapeStr(itemPriceInfo.getString("Buy_Price")) +
                       "</Buy_Price>";
      }

      String firstBidTag = "<First_Bid>$" +
                           xmlEscapeStr(itemInfo.getString("First_Bid")) +
                           "</First_Bid>";

      String nBidsTag = "<Number_of_Bids>" +
                        xmlEscapeStr(itemInfo.getString("Number_of_Bids")) +
                        "</Number_of_Bids>";
    
      String bidsTag = "";
      while (bidInfo.next())
      {
        if (bidsTag.equals(""))
          bidsTag = "<Bids>";
      
        bidsTag += "<Bid>";

        String userID = xmlEscapeStr(bidInfo.getString("BidderID"));

        /* Get bidder's rating from BidderRating table */
        ResultSet ratingInfo = null;
        try
        {
          PreparedStatement ratingStmt = conn.prepareStatement(
            "SELECT Rating FROM BidderRating WHERE ID = ?"
          );
          ratingStmt.setString(1, userID);
          ratingInfo = ratingStmt.executeQuery();
        }
        catch (SQLException ex)
        {
          System.out.println("SQLException (BidderRating): " +
                             ex.getMessage());
          ex.printStackTrace();
        }
        ratingInfo.next(); // This should not return false
        String rating = xmlEscapeStr(ratingInfo.getString("Rating"));

        bidsTag += "<Bidder Rating=\"" + rating +
                   "\" UserID=\"" + userID + "\">";

        /* Get bidder's location from UserLocation table */
        ResultSet userLocInfo = null;
        String userLoc = "";
        String country = "";
        try
        {
          PreparedStatement userLocStmt = conn.prepareStatement(
            "SELECT Location, Country FROM UserLocation WHERE ID = ?"
          );
          userLocStmt.setString(1, userID);
          userLocInfo = userLocStmt.executeQuery();
        }
        catch (SQLException ex)
        {
          System.out.println("SQLException (UserLocation): " +
                             ex.getMessage());
          ex.printStackTrace();
        }
        if (userLocInfo.next())
        {
          if (userLocInfo.getString("Location") != null)
          {
            userLoc = "<Location>" + 
                      xmlEscapeStr(userLocInfo.getString("Location")) +
                      "</Location>";
          }
          if (userLocInfo.getString("Country") != null)
          {
            country = "<Country>" +
                      xmlEscapeStr(userLocInfo.getString("Location")) +
                      "</Country>";
          }
        }

        if (userLoc.equals("") == false)
          bidsTag += "<Location>" + userLoc + "</Location>";
        if (country.equals("") == false)
          bidsTag += "<Country>" + country + "</Country>";
        bidsTag += "</Bidder>";

        String time = xmlEscapeStr(bidInfo.getString("Time"));
        try
        {
          time = xmlFormat.format(dataFormat.parse(time));
        }
        catch (java.text.ParseException ex)
        {
          System.out.println("Parse date error");
          ex.printStackTrace();
        }
        bidsTag += "<Time>" + time + "</Time>";

        String amount = xmlEscapeStr(bidInfo.getString("Amount"));
        bidsTag += "<Amount>$" + amount + "</Amount>";

        bidsTag = "</Bid>";
      }
      if (bidsTag.equals(""))
        bidsTag = "<Bids />";
      else
        bidsTag += "</Bids>";

      String locTag = "<Location";
      /* Get latitude/longitude (if it exists) of item from ItemLocation
       * table */
      ResultSet itemLocInfo = null;
      try
      {
        PreparedStatement itemLocStmt = conn.prepareStatement(
          "SELECT Latitude, Longitude FROM ItemLocation WHERE ItemID = ?"
        );
        itemLocStmt.setString(1, itemId);
        itemLocInfo = itemLocStmt.executeQuery();
      }
      catch (SQLException ex)
      {
        System.out.println("SQLException (ItemLocation): " + ex.getMessage());
        ex.printStackTrace();
      }
      if (itemLocInfo.next())
      {
        if (itemLocInfo.getString("Latitude") != null)
        {
          locTag += " Latitude=\"" +
                    xmlEscapeStr(itemLocInfo.getString("Latitude")) +
                    "\"";
        }
        if (itemLocInfo.getString("Longitude") != null)
        {
          locTag += " Longitude=\"" +
                    xmlEscapeStr(itemLocInfo.getString("Longitude")) +
                    "\"";
        }
      }
      locTag += ">" + xmlEscapeStr(itemInfo.getString("Location")) +
                "</Location>";

      String countryTag = "<Country>" +
                          xmlEscapeStr(itemInfo.getString("Country")) +
                          "</Country>";

      String startedTag = "";
      String endsTag = "";
      try
      {
        String started = xmlEscapeStr(itemInfo.getString("Started"));
        startedTag = "<Started>" +
                     xmlFormat.format(dataFormat.parse(started)) +
                     "</Started>";

        String ends = itemInfo.getString("Ends");
        endsTag = "<Ends>" +
                  xmlFormat.format(dataFormat.parse(ends)) +
                  "</Ends>";
      }
      catch (java.text.ParseException ex)
      {
        System.out.println("Parse date error");
        ex.printStackTrace();
      }

      sellerInfo.next(); // This should not return false
      String sellerTag = "<Seller Rating=\"" +
                         xmlEscapeStr(sellerInfo.getString("Rating")) +
                         "\" UserID=\"" +
                         xmlEscapeStr(itemInfo.getString("SellerID")) +
                         "\" />";

      String descriptionTag = ""; 
      if (itemInfo.getString("Description").equals(""))
        descriptionTag = "<Description />";
      else
      {
        descriptionTag = "<Description>" +
                         xmlEscapeStr(itemInfo.getString("Description")) +
                         "</Description>";
      }

      String itemEndTag = "</Item>";

      xml = itemStartTag + nameTag + categoriesTags + currentlyTag +
            buyPriceTag + firstBidTag + nBidsTag + bidsTag + locTag +
            countryTag + startedTag + endsTag + sellerTag + 
            descriptionTag + itemEndTag;
    }
    catch (SQLException ex)
    {
	  System.out.println("SQLException: " + ex.getMessage());
      ex.printStackTrace();
	}

    // Close the database connection
	try
    {
	  conn.close();
	}
    catch (SQLException ex)
    {
	  System.out.println("SQLException: " + ex.getMessage());
      ex.printStackTrace();
	}
    return xml;
  }
	
  public String echo(String message)
  {
    return message;
  }

}
