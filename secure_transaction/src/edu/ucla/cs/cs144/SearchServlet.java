package edu.ucla.cs.cs144;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet
{
  public SearchServlet() {}

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException
  {
    String q = request.getParameter("q");
    if (q == null || q == "")
    {
      request.setAttribute("invalid", "Please enter one or more keywords.");
      request.getRequestDispatcher("/search.jsp").forward(request, response);
      return;
    }
    request.setAttribute("query", q);

    int nTotalResults = AuctionSearchClient.basicSearch(q, 0, 200000).length;
    request.setAttribute("nTotalResults", nTotalResults);

    String invalid = "Oops, you entered an invalid value for ";
    String skipStr = request.getParameter("numResultsToSkip");
    if (skipStr == null)
    {
      request.setAttribute("invalid",
                           "Please enter the number of results to skip.");
      request.getRequestDispatcher("/search.jsp").forward(request, response);
      return;
    }
    request.setAttribute("skip", skipStr);

    String retStr = request.getParameter("numResultsToReturn");
    if (retStr == null)
    {
      request.setAttribute("invalid",
                           "Please enter the number of results to show.");
      request.getRequestDispatcher("/search.jsp").forward(request, response);
      return;
    }
    request.setAttribute("ret", retStr);

    int skip = 0;
    try
    {
      skip = Integer.parseInt(request.getParameter("numResultsToSkip"));
    }
    catch (NumberFormatException e)
    {
      invalid += "the number of results to skip.";
      request.setAttribute("invalid", invalid);
      request.getRequestDispatcher("/search.jsp").forward(request, response);
      return;
    }

    int ret = 0;
    try
    {
      ret = Integer.parseInt(request.getParameter("numResultsToReturn"));
    }
    catch (NumberFormatException e)
    {
      invalid += "the number of results to return.";
      request.setAttribute("invalid", invalid);
      request.getRequestDispatcher("/search.jsp").forward(request, response);
      return;
    }
    

    SearchResult[] basicResults = AuctionSearchClient.basicSearch(
      q, skip, ret
    );
    request.setAttribute("results", basicResults);

    request.getRequestDispatcher("/search.jsp").forward(request, response);
  }
}
