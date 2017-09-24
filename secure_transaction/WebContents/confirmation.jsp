<!DOCTYPE html>
<html>
    <head>
        <title>Bought <%= request.getAttribute("name") %></title>
        <meta charset="UTF-8" />
    </head>
    <body>
        <%
           if (request.getAttribute("timeout") == null)
           {
        %>
             <h2>Transaction Successful!</h2>
             <div id="infoBox">
                 Item ID: <%= request.getAttribute("itemId") %><br />
                 Name: <%= request.getAttribute("name") %><br />
                 Price Paid: <%= request.getAttribute("buyPrice") %><br />
                 Credit Card Number: 
                   <%= request.getAttribute("creditCardNum") %><br />
                 Time: <%= request.getAttribute("transTime") %>
             </div>
        <%
           }
           else
           {
        %>
             <h2>Session Timeout</h2>
        <%
             out.println("Your payment was not processed.");
             out.print("Please reenter the item ID of the item you wish to " +
               "buy <a href=\"eBay/item\">here</a>.");
           }
        %>
    </body>
</html>
