package com.itsinbox.smartbox.proxy;

import com.itsinbox.smartbox.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ProxyUtils {
   private static String CONFIG_FILE;

   public static void init(String configPath) {
      CONFIG_FILE = configPath;
   }

   public static void setNoProxy(boolean save) {
      unsetProxy();
      if (save) {
         saveProxySettings(new ProxyParams(ProxyType.NO_PROXY));
      }

   }

   public static void setSystemProxy(boolean save) {
      unsetProxy();
      System.setProperty("java.net.useSystemProxies", "true");
      if (save) {
         saveProxySettings(new ProxyParams(ProxyType.SYSTEM_PROXY));
      }

   }

   public static void setHttpProxy(String host, String port, boolean save) {
      unsetProxy();
      System.setProperty("http.proxyHost", host);
      System.setProperty("http.proxyPort", port);
      System.setProperty("https.proxyHost", host);
      System.setProperty("https.proxyPort", port);
      if (save) {
         saveProxySettings(new ProxyParams(ProxyType.HTTP_PROXY, host, port));
      }

   }

   public static void setSocksProxy(String host, String port, boolean save) {
      unsetProxy();
      System.setProperty("socksProxyHost", host);
      System.setProperty("socksProxyPort", port);
      if (save) {
         saveProxySettings(new ProxyParams(ProxyType.SOCKS_PROXY, host, port));
      }

   }

   private static void unsetProxy() {
      System.clearProperty("http.proxyHost");
      System.clearProperty("http.proxyPort");
      System.clearProperty("https.proxyHost");
      System.clearProperty("https.proxyPort");
      System.clearProperty("socksProxyHost");
      System.clearProperty("socksProxyPort");
      System.setProperty("java.net.useSystemProxies", "false");
   }

   public static ProxyParams readProxySettings() {
      String confLocation = System.getProperty("user.home") + File.separator + CONFIG_FILE;
      FileInputStream configFile = null;

      ProxyParams proxyParams;
      try {
         Properties config = new Properties();
         configFile = new FileInputStream(confLocation);
         config.load(configFile);
         ProxyType proxyType = ProxyType.fromOrdinal(Integer.parseInt(config.getProperty("proxyType")));
         String proxyHost = config.getProperty("proxyHost");
         String proxyPort = config.getProperty("proxyPort");
         proxyParams = new ProxyParams(proxyType, proxyHost, proxyPort);
      } catch (Exception var15) {
         Utils.logMessage("Config read error! " + var15.getMessage());
         proxyParams = new ProxyParams(ProxyType.NO_PROXY);
      } finally {
         try {
            if (configFile != null) {
               configFile.close();
            }
         } catch (IOException var14) {
            Utils.logMessage("Config read error! " + var14.getMessage());
         }

      }

      if (proxyParams != null && proxyParams.getProxyType() != null) {
         applyProxySettings(proxyParams, false);
      }

      return proxyParams;
   }

   private static void applyProxySettings(ProxyParams proxyParams, boolean save) {
      switch(proxyParams.getProxyType()) {
      case NO_PROXY:
         setNoProxy(save);
         break;
      case SYSTEM_PROXY:
         setSystemProxy(save);
         break;
      case HTTP_PROXY:
         setHttpProxy(proxyParams.getProxyHost(), proxyParams.getProxyPort(), save);
         break;
      case SOCKS_PROXY:
         setSocksProxy(proxyParams.getProxyHost(), proxyParams.getProxyPort(), save);
      }

   }

   private static void saveProxySettings(ProxyParams proxyParams) {
      String confLocation = System.getProperty("user.home") + File.separator + CONFIG_FILE;
      FileOutputStream configFile = null;

      try {
         Properties config = new Properties();
         configFile = new FileOutputStream(confLocation);
         config.setProperty("proxyType", String.valueOf(proxyParams.getProxyType().getValue()));
         if (proxyParams.getProxyHost() != null) {
            config.setProperty("proxyHost", proxyParams.getProxyHost());
         }

         if (proxyParams.getProxyPort() != null) {
            config.setProperty("proxyPort", proxyParams.getProxyPort());
         }

         config.store(configFile, (String)null);
      } catch (IOException var12) {
         Utils.logMessage("Config save error! " + var12.getMessage());
      } finally {
         try {
            if (configFile != null) {
               configFile.close();
            }
         } catch (IOException var11) {
            Utils.logMessage("Config save error! " + var11.getMessage());
         }

      }

   }
}
