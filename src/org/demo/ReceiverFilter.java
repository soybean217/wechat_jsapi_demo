package org.demo;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.common.util.ConfigManager;

public class ReceiverFilter implements Filter {

  private static final Logger LOG = Logger.getLogger(ReceiverFilter.class);

  private List<String> exceptList;

  public void destroy() {
    // TODO Auto-generated method stub

  }

  private void writeTo(String fileName, byte[] body) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(ConfigManager.getConfigData("save.body.path") + fileName);
    fileOutputStream.write(body);
    fileOutputStream.flush();
    fileOutputStream.close();
  }

  private byte[] readBody(HttpServletRequest request) throws IOException {
    // 获取请求文本字节长度
    int formDataLength = request.getContentLength();
    // 取得ServletInputStream输入流对象
    DataInputStream dataStream = new DataInputStream(request.getInputStream());
    byte body[] = new byte[formDataLength];
    int totalBytes = 0;
    while (totalBytes < formDataLength) {
      int bytes = dataStream.read(body, totalBytes, formDataLength);
      totalBytes += bytes;
    }
    return body;
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    LOG.debug(req.getMethod() + ":" + req.getRequestURI() + "?"
        + (req.getQueryString() != null ? req.getQueryString() : ""));

    if (req.getContentLength() > 0) {
      byte[] body = readBody(req);
      String saveFileName = System.currentTimeMillis() + ".txt";
      writeTo(saveFileName, body);
    }

    logBody(request);
    java.util.Enumeration headerNames = req.getHeaderNames();
    StringBuffer header = new StringBuffer();
    while (headerNames.hasMoreElements()) {
      String headerKey = (String) headerNames.nextElement();
      header.append("\n" + headerKey + ":" + req.getHeader(headerKey));
    }
    LOG.debug(header);

    String fileName = req.getRequestURI().split("/")[req.getRequestURI().split("/").length - 1];
    boolean bExcept = false;
    for (String prefix : exceptList) {
      if (fileName.startsWith(prefix)) {
        bExcept = true;
        break;
      }
    }
    if (bExcept) {
      chain.doFilter(request, response);
    } else {
      request.getRequestDispatcher("index.jsp").forward(request, response);
    }
  }

  private void logBody(ServletRequest request) throws IOException, UnsupportedEncodingException {
    String info = null;
    int len = 0;
    int temp = 0;
    InputStream is = request.getInputStream();
    byte[] b = new byte[1000000];
    while ((temp = is.read()) != -1) {
      b[len] = (byte) temp;
      len++;
    }
    is.close();
    info = new String(b, 0, len, "utf-8");
    if (info != null & info.length() > 0) {
      LOG.debug("####receive post:\n" + info);
      LOG.debug("####end:");
    }
  }

  public void init(FilterConfig filterConfig) throws ServletException {
    String exceptPages = filterConfig.getInitParameter("exceptPages");
    if (exceptPages != null) {
      exceptList = Arrays.asList(exceptPages.split(";"));
    } else {
      exceptList = Collections.emptyList();
    }
  }

}
