package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import javax.servlet.Servlet;
import javax.servlet.ServletContext; // Debug
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet
{
  public ProxyServlet() {}

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException
  {
    ServletContext context = getServletContext();
    String query = request.getParameter("q");
    context.log(query + "\n");
    
    HttpURLConnection googleCon = null;     
    try
    {
      URL googleURL = new URL(
        "http://www.google.com/complete/search?output=toolbar&q=" +
        URLEncoder.encode(query, "UTF-8")
      );
      URLConnection con = googleURL.openConnection();
      googleCon = (HttpURLConnection)con;
    }
    catch (MalformedURLException e)
    {
      System.err.println("Malformed URL");
    }
    catch (UnsupportedEncodingException e)
    {
      System.err.println("Error encoding");
    }
    catch (IOException e)
    {
      System.err.println("Error opening connection");
    }

    String content = "";
    try
    {
      BufferedReader in = new BufferedReader(
        new InputStreamReader(googleCon.getInputStream())
      );
      String current;
      while ((current = in.readLine()) != null)
      {
        content += current;
      }
    }
    catch (IOException e)
    {
      System.err.println("I/O error");
    }
    
    response.setContentType("text/xml");
    response.getWriter().write(content);
  }
}
