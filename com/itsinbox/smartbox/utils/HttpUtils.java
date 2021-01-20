package com.itsinbox.smartbox.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {
   private static final String USER_AGENT = "SmartBoxApp";
   private static final String ENCODING = "UTF-8";

   public static String sendHttpGet(String url) throws Exception {
      return sendHttpRequest("GET", url, (HashMap)null);
   }

   public static String sendHttpsGet(String url) throws Exception {
      return sendHttpsRequest("GET", url, (HashMap)null);
   }

   public static String sendHttpPost(String url, HashMap params) throws Exception {
      return sendHttpRequest("POST", url, params);
   }

   public static String sendHttpsPost(String url, HashMap params) throws Exception {
      return sendHttpsRequest("POST", url, params);
   }

   public static String sendHttpRequest(String method, String url, HashMap params) throws Exception {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection)obj.openConnection();
      con.setRequestMethod(method);
      con.setRequestProperty("User-Agent", "SmartBoxApp");
      con.setRequestProperty("Accept-Charset", "UTF-8");
      if (params != null) {
         String urlParameters = buildParams(params);
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.writeBytes(urlParameters);
         wr.flush();
         wr.close();
      }

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder response = new StringBuilder();

      String inputLine;
      while((inputLine = in.readLine()) != null) {
         response.append(inputLine);
      }

      in.close();
      return response.toString();
   }

   public static String sendHttpsRequest(String method, String url, HashMap params) throws Exception {
      URL obj = new URL(url);
      HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
      con.setRequestMethod(method);
      con.setRequestProperty("User-Agent", "SmartBoxApp");
      con.setRequestProperty("Accept-Charset", "UTF-8");
      if (params != null) {
         String urlParameters = buildParams(params);
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.writeBytes(urlParameters);
         wr.flush();
         wr.close();
      }

      int responseCode = con.getResponseCode();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder response = new StringBuilder();

      String inputLine;
      while((inputLine = in.readLine()) != null) {
         response.append(inputLine);
      }

      in.close();
      return response.toString();
   }

   public static String buildParams(HashMap params) {
      StringBuilder sbParams = new StringBuilder();
      int i = 0;

      for(Iterator var3 = params.keySet().iterator(); var3.hasNext(); ++i) {
         String key = (String)var3.next();

         try {
            if (i != 0) {
               sbParams.append("&");
            }

            sbParams.append(key).append("=").append(URLEncoder.encode((String)params.get(key), "UTF-8"));
         } catch (UnsupportedEncodingException var6) {
            Utils.logMessage("HTTP params encoding error: " + var6);
         }
      }

      return sbParams.toString();
   }
}
