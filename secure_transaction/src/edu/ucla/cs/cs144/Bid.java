package edu.ucla.cs.cs144;

public class Bid
{
  public class Bidder
  {
    private String id;
    private String rating;
    private String location;
    private String country;

    public Bidder(String id, String rating, String location, String country)
    {
      this.id = id;
      this.rating = rating;
      this.location = location;
      this.country = country;
    }

    public String getId()
    {
      return id;
    }

    public String getRating()
    {
      return rating;
    }

    public String getLocation()
    {
      return location;
    }

    public String getCountry()
    {
      return country;
    }
  }

  private Bidder bidder;
  private String time;
  private String amount;

  public Bid(String id, String rating, String location, String country,
             String time, String amount)
  {
    bidder = new Bidder(id, rating, location, country);
    this.time = time;
    this.amount = amount;
  }

  public Bidder getBidder()
  {
    return bidder;
  }

  public String getTime()
  {
    return time;
  }

  public String getAmount()
  {
    return amount;
  }
}
