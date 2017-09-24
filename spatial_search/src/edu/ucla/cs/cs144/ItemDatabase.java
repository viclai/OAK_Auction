package edu.ucla.cs.cs144;

import java.util.ArrayList;

public class ItemDatabase
{
  private static ArrayList<Item> items = null;

  public static void addItem(String id, String name, String description,
                             ArrayList<String> categories)
  {
    Item item = new Item(id, name, description, categories);
    if (items == null)
      items = new ArrayList<Item>();
    items.add(item);
  }

  public static Item[] getItems()
  {
    Item itemArr[] = new Item[items.size()];
    return items.toArray(itemArr);
  }
}
