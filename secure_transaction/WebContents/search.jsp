<!DOCTYPE html>
<%@ page import="edu.ucla.cs.cs144.SearchResult" %>
<html>
    <%
       String textBoxVal = "";
       if (request.getAttribute("query") != null)
         textBoxVal = (String)request.getAttribute("query");
    %>
    <head>
        <title><%= textBoxVal %> - Auction Search</title>
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
                 textBoxVal + "\" " + "autocomplete=\"off\" />");
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
             if ((Integer)request.getAttribute("nTotalResults") == 0)
               out.print("No results found");
             else
             {
               int skipped =
                 Integer.parseInt((String)request.getAttribute("skip"));
               int nReturned =
                 Integer.parseInt((String)request.getAttribute("ret"));
               int n = skipped + nReturned;
               boolean lastPage = false;
               if (n > (Integer)request.getAttribute("nTotalResults"))
               {
                 n = Integer.parseInt(
                   request.getAttribute("nTotalResults").toString()
                 );
                 lastPage = true;
               }
        %>
               <div>
                   Showing  
                   <% out.print(skipped + 1); %>-<% out.print(n); %> of 
                   <% out.print(request.getAttribute("nTotalResults")); %> 
                   results
               </div>

               <ul>
        <%
               for (SearchResult res : results)
               {
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
                   <%
                      if (skipped >= nReturned)
                      {
                   %>
                        <button type="button" onclick="prev();">Back</button>
                   <%
                      }
                      else
                      {
                   %>
                        <button type="button" disabled>Back</button>
                   <%
                      }

                      if (!lastPage)
                      { 
                   %>
                        <button type="button" onclick="next();">Next</button>
                   <%
                      }
                      else
                      {
                   %>
                        <button type="button" disabled>Next</button>
                   <% } %>
               </div>
        <%   
             }
           }
        %>
    </body>
</html>
