package org.demo;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class Wechat
 */
@WebServlet("/Wechat")
public class Wechat extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(Wechat.class);
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public Wechat() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.getWriter().print(request.getParameter("echostr"));
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    printBodyString(request);
  }

  private void printBodyString(HttpServletRequest request) throws IOException {
    // Read from request
    StringBuffer buffer = new StringBuffer();
    BufferedReader reader = request.getReader();
    String line;
    while ((line = reader.readLine()) != null) {
      buffer.append(line);
    }
    String info = buffer.toString();
    LOG.debug(info);
  }

}
