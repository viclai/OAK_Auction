<!DOCTYPE html>
<%@ page import="edu.ucla.cs.cs144.SearchResult" %>
<html>
    <head>
        <title><%= request.getAttribute("query") %> - Auction Search</title>
        <meta charset="UTF-8" />
        <link rel="stylesheet" type="text/css" href="css/suggestions.css" />
        <script src="js/autosuggest.js" type="text/javascript"></script>
        <script src="js/search.js" type="text/javascript"></script>
        <script type="text/javascript">
            window.onload = function() {
                var oTextbox = new AutoSuggestControl(
                    document.getElementsByName("q")[0],
                    new SuggestionProvider()
                );
            }
        </script>
    </head>
    <body>
        <form action="/eBay/search" id="searchForm" method="GET">
            <%
               out.println("<input type=\"text\" name=\"q\" value=\"" +
                 request.getAttribute("query") + "\" " +
                 "autocomplete=\"off\" />");
            %>
            <input type="submit" value="Search" onclick="firstPage();" />
            <%
               out.println("<input type=\"hidden\" " + 
                 "name=\"numResultsToSkip\" value=\"" + 
                 request.getAttribute("skip") + "\" />");
               out.println("<input type=\"hidden\" " + 
                 "name=\"numResultsToReturn\" value=\"" + 
                 request.getAttribute("ret") + "\" />");
            %>
        </form>
        <br />
        <%
           SearchResult[] results = null;
           if (request.getAttribute("invalid") != null)
             out.print(request.getAttribute("invalid"));
           else
           {
             results = (SearchResult[])request.getAttribute("results");
             if (results.length == 0) {
               out.print("No results found");
        %>
        <div>
            <input type="button" value="Previous" onclick="prev();" />
        </div>
        <%
             } else {
        %>
        <ul>
            <%
               for (SearchResult res : results) {
            %>
            <li>
                <%
                   out.print("<a href=\"/eBay/item?id=" + res.getItemId() + 
                       "\">" + res.getItemId() + "</a>: " + res.getName());
                %>
            </li>
            <%   
               }
            %>
        </ul>
        <div>
            <input type="button" value="Back" onclick="prev();" />
            <input type="button" value="Next" onclick="next();" />
        </div>
        <%   
             }
        %>
        <%
           }
        %>
    </body>
</html>
