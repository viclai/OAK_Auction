package edu.ucla.cs.cs144;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PayServlet extends HttpServlet implements Servlet
{
  public PayServlet() {}

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException
  {
    boolean timeout = true;
    HttpSession session = request.getSession(true);

    String itemId = (String)session.getAttribute("itemId");
    if (itemId == null)
    {
      request.setAttribute("timeout", timeout);
      request.getRequestDispatcher("/purchase.jsp").forward(request, response);
      return;
    }
    request.setAttribute("itemId", itemId);

    String auction = (String)session.getAttribute("name");
    request.setAttribute("name", auction);

    String buyPrice = (String)session.getAttribute("buyPrice");
    request.setAttribute("buyPrice", buyPrice);

    request.getRequestDispatcher("/purchase.jsp").forward(request, response);
  }
}

