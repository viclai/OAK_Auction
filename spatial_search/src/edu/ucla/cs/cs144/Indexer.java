package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer
{
  private IndexWriter indexWriter = null;
    
  /** Creates a new instance of Indexer */
  public Indexer() {}

  public IndexWriter getIndexWriter()
  {
    if (indexWriter == null)
    {
      try
      {
        Directory indexDir = FSDirectory.open(
          new File("/var/lib/lucene/")
        );
        IndexWriterConfig config = new IndexWriterConfig(
          Version.LUCENE_4_10_2, new StandardAnalyzer()
        );
        indexWriter = new IndexWriter(indexDir, config);
      }
      catch (IOException ex)
      {
        System.err.println("IOException: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
    return indexWriter;
  }

  public void closeIndexWriter() throws IOException
  {
    if (indexWriter != null)
      indexWriter.close();
  }

  public void indexItem(Item item) throws IOException
  {
    IndexWriter writer = getIndexWriter();
    Document doc = new Document();
    doc.add(new StringField("ItemID", item.getId(), Field.Store.YES));
    doc.add(new StringField("Name", item.getName(), Field.Store.YES));
    String searchableText = item.getName() + " " + item.getDescription() + " ";
    String[] categories = item.getCategories();
    for (String i : categories)
      searchableText += i + " ";
    searchableText = searchableText.substring(0, searchableText.length() - 1);
    doc.add(new TextField("content", searchableText, Field.Store.NO));
    writer.addDocument(doc);
  }

  public void rebuildIndexes()
  {
    Connection conn = null;

    // Create a connection to the database to retrieve Items from MySQL
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

    getIndexWriter();

    ResultSet items = null;
    try
    {
      PreparedStatement itemsStmt = conn.prepareStatement(
        "SELECT ItemID, Name, Description FROM Item"
      );
      items = itemsStmt.executeQuery();
    
      while (items.next())
      {
        String id = items.getString("ItemID");
        String name = items.getString("Name");
        String description = items.getString("Description");

        ArrayList<String> categories = new ArrayList<String>();
        ResultSet ctgs = null;
        PreparedStatement ctgStmt = conn.prepareStatement(
          "SELECT Category FROM ItemCategory WHERE ItemID = ?"
        );
        ctgStmt.setString(1, id);
        ctgs = ctgStmt.executeQuery();

        while (ctgs.next())
        {
          categories.add(ctgs.getString("Category"));
        }

        ItemDatabase.addItem(id, name, description, categories);
      }
    }
    catch (SQLException ex)
    {
      System.err.println("SQLException: " + ex.getMessage());
      ex.printStackTrace();
    }

    // Create indexes
    Item allItems[] = ItemDatabase.getItems();
    try
    {
      for (Item i : allItems)
        indexItem(i);
      closeIndexWriter();
    }
    catch (IOException ex)
    {
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
  }    

  public static void main(String args[])
  {
    Indexer idx = new Indexer();
    idx.rebuildIndexes();
  }   
}
