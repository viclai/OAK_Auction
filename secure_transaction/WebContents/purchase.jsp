<!DOCTYPE html>
<html>
    <head>
        <title>Purchasing <%= request.getAttribute("name") %></title>
        <meta charset="UTF-8" />
    </head>
    <body>
        <%
           if (request.getAttribute("timeout") == null)
           {
        %>
             <h2>Please enter your credit card information</h2>
             <div id="infoBox">
                 Item ID: <%= request.getAttribute("itemId") %><br />
                 Name: <%= request.getAttribute("name") %><br />
                 Price: <%= request.getAttribute("buyPrice") %><br />
             </div>
             <form action="https://localhost:8443/eBay/confirmation"
                 method="POST">
                 Credit Card Number: <input type="text" name="creditCardNum" />
                 <input type="submit" value="Submit" />
             </form>
        <%
           }
           else
           {
        %>
             <h2>Session Timeout</h2>
        <%
             out.print("Please reenter the item ID of the item you wish to " +
               "buy <a href=\"eBay/item\">here</a>.");
           }
        %>
    </body>
</html>
