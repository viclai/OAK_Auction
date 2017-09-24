package edu.ucla.cs.cs144;

import java.util.Calendar;

import java.io.IOException;

import java.text.SimpleDateFormat;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfirmPaymentServlet extends HttpServlet implements Servlet
{
  public ConfirmPaymentServlet() {}

  protected void doPost(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException
  {
    boolean timeout = true;
    HttpSession session = request.getSession(true);

    String itemId = (String)session.getAttribute("itemId");
    if (itemId == null)
    {
      request.setAttribute("timeout", timeout);
      request.getRequestDispatcher("/confirmation.jsp").forward(
        request, response
      );
      return;
    }
    request.setAttribute("itemId", itemId);

    String auction = (String)session.getAttribute("name");
    request.setAttribute("name", auction);

    String buyPrice = (String)session.getAttribute("buyPrice");
    request.setAttribute("buyPrice", buyPrice);

    String creditCard = (String)request.getParameter("creditCardNum");
    request.setAttribute("creditCardNum", creditCard);

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy  hh:mm:ss");
    String time = sdf.format(cal.getTime());
    request.setAttribute("transTime", time);

    request.getRequestDispatcher("/confirmation.jsp").forward(
      request, response
    );
  }
}


