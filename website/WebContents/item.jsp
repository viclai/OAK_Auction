<!DOCTYPE html>
<%@ page import="edu.ucla.cs.cs144.Bid" %>
<html>
    <head>
        <title><%= request.getAttribute("itemId") %> - Item Search</title>
        <meta chaset="UTF-8" />
        <script type="text/javascript" src="js/item.js"></script>
        <script type="text/javascript" 
            src="http://maps.google.com/maps/api/js?sensor=false"></script> 
    </head>
    <%
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
            <input type="text" name="id" />
            <input type="submit" value="Search" />
        </form>
        <br />
        <%
           if (request.getAttribute("invalid") != null)
           {
             if (request.getAttribute("itemId").equals(""))
               out.print("Please enter an item ID.");
             else
               out.print(request.getAttribute("invalid"));
           }
           else {
        %>
        <div id="auctionName">
            Auction Name: <%= request.getAttribute("name") %>
        </div>
        <div id="categories">
            Categories: 
            <% 
               String[] categories =
                 (String[])request.getAttribute("categories");
               for (String category : categories)
               {
                 out.print(category + " ");
               }
            %>
        </div>
        <div id="currently">
            Current Price: <%= request.getAttribute("currently") %>
        </div>
        <div id="firstBid">
            First Bid: <%= request.getAttribute("firstBid") %>
        </div>
        <div id="nBids">
            Number of Bids: <%= request.getAttribute("nBids") %>
        </div>
        <div id="bids">
            <% 
               Bid[] bids = (Bid[])request.getAttribute("bids");
               if (bids.length != 0) {
            %>
            <h2>Bids Placed</h2>
            <% } %>
            <%
               for (Bid bid : bids) {
            %>
            <div class="bid">
                At <% out.print(bid.getTime()); %>:<br />
                Bidder <% out.print(bid.getBidder().getId()); %> 
                (<% out.print(bid.getBidder().getRating()); %>) from 
                <% out.print(bid.getBidder().getLocation()); %>, 
                <% out.print(bid.getBidder().getCountry()); %> placed a bid of 
                <% out.print(bid.getAmount()); %> for this item.
            </div>
            <br />
            <% } %>
        </div>
        <div id="location">
            Location: <%= request.getAttribute("location") %>
            <%
               if (!request.getAttribute("latitude").equals("") &&
                   !request.getAttribute("longitude").equals("")) {
            %>
            (<% out.print(request.getAttribute("latitude")); %>, 
            <% out.print(request.getAttribute("longitude")); %>)
            <br />
            <% } %>
            <div id="map" style="width:500px; height:500px; border:solid">
            </div>
        </div>
        <div id="country">
            Country: <%= request.getAttribute("country") %>
        </div>
        <div id="started">
            Start Time: <%= request.getAttribute("started") %>
        </div>
        <div id="ends">
            End Time: <%= request.getAttribute("ends") %>
        </div>
        <div id="seller">
            Auctioned by: <%= request.getAttribute("sellerId") %>
            (<%= request.getAttribute("sellerRating") %>)
        </div>
        <div id="description">
            Description:<br />
            <%= request.getAttribute("description") %>
        </div>
        <% } %>
    </body>
</html>
