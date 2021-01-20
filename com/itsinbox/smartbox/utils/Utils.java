package com.itsinbox.smartbox.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
   private static FileLogger logger;

   private Utils() {
   }

   public static String int2HexString(int i) {
      return bytes2HexString(asByteArray(i >>> 24, i >>> 16, i >>> 8, i));
   }

   public static String bytes2HexString(byte... bytes) {
      List builder = new ArrayList();
      boolean skipZeros = true;
      byte[] var3 = bytes;
      int var4 = bytes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte b = var3[var5];
         if (!skipZeros || b != 0) {
            skipZeros = false;
            builder.add(String.format("%02X", b));
         }
      }

      StringBuilder sb = new StringBuilder();
      Iterator var8 = builder.iterator();

      while(var8.hasNext()) {
         String s = (String)var8.next();
         sb.append(s).append(":");
      }

      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }

   private static byte asByte(int value) {
      return (byte)(value & 255);
   }

   public static byte[] asByteArray(int... values) {
      byte[] valueBytes = new byte[values.length];

      for(int i = 0; i < values.length; ++i) {
         valueBytes[i] = asByte(values[i]);
      }

      return valueBytes;
   }

   public static void openURL(String url) throws IOException {
      String os = System.getProperty("os.name").toLowerCase();
      Runtime rt = Runtime.getRuntime();

      try {
         if (os.contains("win")) {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
         } else if (os.contains("mac")) {
            rt.exec("open " + url);
         } else if (os.contains("nix") || os.contains("nux")) {
            rt.exec("xdg-open " + url);
         }
      } catch (IOException var4) {
         logMessage("Ne mogu da otvorim browser!");
      }

   }

   public static void logMessage(String message) {
      System.out.println(message);
      if (logger == null) {
         logger = new FileLogger();
      }

      logger.writeToLog(message);
   }
}
