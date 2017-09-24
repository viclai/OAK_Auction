<!DOCTYPE html>
<%@ page import="edu.ucla.cs.cs144.Bid" %>
<html>
    <%
       String id = "";
       if (request.getAttribute("itemId") != null)
         id = (String)request.getAttribute("itemId");
    %>
    <head>
        <title><%= id %> - Item Search</title>
        <meta chaset="UTF-8" />
        <link rel="stylesheet" type="text/css" href="css/item.css" />
        <script type="text/javascript" src="js/item.js"></script>
        <script type="text/javascript" 
            src="http://maps.google.com/maps/api/js?sensor=false"></script> 
    </head>
    <% // Body tag
       if (request.getAttribute("invalid") == null &&
           !request.getAttribute("latitude").equals("") &&
           !request.getAttribute("longitude").equals(""))
       {
         out.println("<body onload=\"initMap(" +
           request.getAttribute("latitude") + ", " +
           request.getAttribute("longitude") + ");\">");
       }
       else
         out.println("<body onload=\"initMap(0, 0);\">");
    %>
        <form action="/eBay/item" method="GET">
            <%
               out.println("<input type=\"text\" name=\"id\" " +
                 "value=\"" + id + "\" />");
            %>
            <input type="submit" value="Search" />
        </form>
        <br />
        <%
           if (request.getAttribute("invalid") != null) // Invalid
             out.print(request.getAttribute("invalid"));
           else
           {
        %>
             <div id="auctionName">
                 <span class="info">Auction Name</span>: 
                 <%= request.getAttribute("name") %>
             </div>
             <div id="categories">
                 <span class="info">Categories</span>: 
                 <%
                    String[] categories =
                      (String[])request.getAttribute("categories");
                    for (String category : categories)
                    {
                 %>
                      <span class="category"><% out.print(category); %>
                      </span>&nbsp;
                 <% } %>
             </div>
        
             <div id="currently">
                 <span class="info">Current Price</span>: 
                 <%= request.getAttribute("currently") %>
             </div>
             <div id="firstBid">
                 <span class="info">First Bid</span>: 
                 <%= request.getAttribute("firstBid") %>
             </div>
             <%
                if (request.getAttribute("buyPrice") != null)
                {
             %>
                  <div id="buyPrice">
                      <span class="info">Buy Price</span>: 
                      <%= request.getAttribute("buyPrice") %>
                      &nbsp;&nbsp;
                      <a href="/eBay/purchase">Pay Now</a>
                  </div>
             <% } %>
             <div id="nBids">
                 <span class="info">Number of Bids</span>: 
                     <%= request.getAttribute("nBids") %>
             </div>
             <div id="bids">
                 <% 
                    Bid[] bids = (Bid[])request.getAttribute("bids");
                    if (bids.length != 0)
                    {
                 %>
                      <h2>Bids History</h2>
                 <%
                    }

                    for (Bid bid : bids)
                    {
                 %>
                      <div class="bid">
                          <% out.print(bid.getTime()); %>:<br />
                          User 
                          <span class="user">
                              <% out.print(bid.getBidder().getId()); %>
                          </span> 
                          (<% out.print(bid.getBidder().getRating()); %>) from 
                          <% out.print(bid.getBidder().getLocation()); %>, 
                          <% out.print(bid.getBidder().getCountry()); %> 
                          placed a bid of <% out.print(bid.getAmount()); %> 
                          for this item.
                      </div>
                      <br />
                 <% } %>
             </div>
             <div id="started">
                 <span class="info">Start Time</span>: 
                 <%= request.getAttribute("started") %>
             </div>
             <div id="ends">
                 <span class="info">End Time</span>: 
                 <%= request.getAttribute("ends") %>
             </div>
             <div id="seller">
                 <span class="info">Auctioned by</span>: 
                 <span class="user">
                     <%= request.getAttribute("sellerId") %>
                 </span>
                 (<%= request.getAttribute("sellerRating") %>)
             </div>
             <div id="description">
                 <span class="info">Description</span>:<br />
                 <%= request.getAttribute("description") %>
             </div>
             <div id="country">
                 <span class="info">Country</span>: 
                 <%= request.getAttribute("country") %>
             </div>
             <div id="location">
                 <span class="info">Location</span>: 
                 <%= request.getAttribute("location") %>
                 <%
                    if (!request.getAttribute("latitude").equals("") &&
                        !request.getAttribute("longitude").equals(""))
                    {
                 %>
                      (<%
                          out.print(request.getAttribute("latitude"));
                       %>&deg;, 
                      <%
                         out.print(request.getAttribute("longitude"));
                      %>&deg;)
                      <br />
                 <% } %>
                 <div id="map"></div>
             </div>
        <% } %>
    </body>
</html>
