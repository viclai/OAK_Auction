package edu.ucla.cs.cs144;

import java.util.ArrayList;

public class Item
{
  private String id;
  private String name;
  private String description;
  private ArrayList<String> categories;

  public Item() {}

  public Item(String id, String name, String description,
              ArrayList<String> categories)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.categories = new ArrayList<String>(categories);
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public String[] getCategories()
  {
    if (categories == null)
      return null;
    String ctgsArr[] = new String[categories.size()];
    return categories.toArray(ctgsArr);
  }

  public void print()
  {
    String[] ctgs = getCategories();
    String printedCtgs = "";
    for (String i : ctgs)
      printedCtgs += i + ", ";
    printedCtgs = printedCtgs.substring(0, printedCtgs.length() - 2);

    System.out.println(
      "ItemID: " + this.id + ", Name: " + this.name + ", Description: " +
      this.description + ", Categories: " + printedCtgs
    );
  }
}
