package com.itsinbox.smartbox;

import com.itsinbox.smartbox.gui.LoginFrame;
import com.itsinbox.smartbox.gui.SettingsFrame;
import com.itsinbox.smartbox.gui.SignXmlFrame;
import com.itsinbox.smartbox.proxy.ProxyParams;
import com.itsinbox.smartbox.proxy.ProxyUtils;
import com.itsinbox.smartbox.utils.Utils;
import java.awt.Component;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

interface OpenUriAppleEventHandler {
   public void handleURI(URI uri);
}

class OpenURIEventInvocationHandler implements InvocationHandler {

   private OpenUriAppleEventHandler urlHandler;

   public OpenURIEventInvocationHandler(OpenUriAppleEventHandler urlHandler) {
      this.urlHandler = urlHandler;
   }

   @SuppressWarnings({ "rawtypes", "unchecked"})
   public Object invoke(Object proxy, Method method, Object[] args) {
      if (method.getName().equals("openURI")) {
         try {
            Class openURIEventClass = Class.forName("com.apple.eawt.AppEvent$OpenURIEvent");
            Method getURLMethod = openURIEventClass.getMethod("getURI");
            //arg[0] should be an instance of OpenURIEvent
            URI uri =  (URI)getURLMethod.invoke(args[0]);
            urlHandler.handleURI(uri);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return null;
   }
}

class OSXAppleEventHelper {
   /**
    * Call only on OS X
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static void setOpenURIAppleEventHandler(OpenUriAppleEventHandler urlHandler) {
      try {
         Class applicationClass = Class.forName("com.apple.eawt.Application");
         Method getApplicationMethod = applicationClass.getDeclaredMethod("getApplication", (Class[])null);
         Object application = getApplicationMethod.invoke(null, (Object[])null);

         Class openURIHandlerClass = Class.forName("com.apple.eawt.OpenURIHandler", false, applicationClass.getClassLoader());
         Method setOpenURIHandlerMethod = applicationClass.getMethod("setOpenURIHandler", openURIHandlerClass);

         OpenURIEventInvocationHandler handler = new OpenURIEventInvocationHandler(urlHandler);
         Object openURIEvent = Proxy.newProxyInstance(openURIHandlerClass.getClassLoader(), new Class[] { openURIHandlerClass }, handler);
         setOpenURIHandlerMethod.invoke(application, openURIEvent);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}

public class SmartBox {
   public static final String VERSION = "1.2.2";
   public static final String ITO_BASE_URL = "http://10.1.65.31";
   public static final String ETO_BASE_URL = "https://test.purs.gov.rs";
   public static final String PRODUCTION_BASE_URL = "https://eporezi.purs.gov.rs";
   public static final String VERSION_CHECK_URL = "https://eporezi.purs.gov.rs/upload/eporezi/version";
   public static final String APP_DOWNLOAD_BASE_URL = "https://eporezi.purs.gov.rs/upload/eporezi/eporezi_setup_v";
   public static final String LOG_FILE_NAME = "eporezi.log";
   public static final String PROXY_CONFIG_FILE = "eporezi_proxy.conf";
   public static final String NOTIFICATION_ENVIRNOMENT_NOT_DETECTED = "Грешка приликом читања параметара.";
   public static final String NOTIFICATION_LOGGING_IN = "Приступање порталу еПорези...";
   public static final String NOTIFICATION_LOGGING_IN_TEST = "Приступање Тестном окружењу...";
   public static final String NOTIFICATION_NO_READER = "Читач картица није пронађен.";
   public static final String NOTIFICATION_NO_CARD = "Картица није пронађена. Молим, убаците картицу у читач.";
   public static final String NOTIFICATION_SERVER_ERROR = "Грешка у комуникацији са сервером.";
   public static final String NOTIFICATION_STATUS_READY = "Читач и картица препознати.";
   public static final String NOTIFICATION_CARD_BROKEN = "Дисфункционална картица.";
   public static final String NOTIFICATION_CARD_BLOCKED = "Блокирана картица.";
   public static final String NOTIFICATION_NO_CERT_DATA = "Дошло је до грешке приликом читања сертификата. Молим, покушајте поново.";
   public static final String NOTIFICATION_WRONG_PIN = "Погрешан ПИН!";
   public static final String NOTIFICATION_PIN_VALID = "ПИН исправан! Учитавање...";
   public static final String NOTIFICATION_INVALID_CERT = "<html>Невалидан сертификат. Обратите се вашем<br>сертификационом телу за помоћ.</html>";
   public static final String WINDOW_TITLE = "еПорези ";
   public static LoginFrame loginFrame;
   private static SmartBox.Environment environment;
   private static String baseUrl;
   private static ProxyParams proxyParams;


   public static void main(String[] args) {
      setLaf();
      ProxyUtils.init(SmartBox.PROXY_CONFIG_FILE);
      proxyParams = ProxyUtils.readProxySettings();


      OSXAppleEventHelper.setOpenURIAppleEventHandler(new OpenUriAppleEventHandler() {
         @Override
         public void handleURI(URI url) {
            loginFrame.dispose();
            processUrl(url.toString());
         }
      });

      showLogin((String) null);
   }

   private static void processUrl(String uri) {
      try {
         Utils.logMessage("processUrl: " + uri);
         uri = uri.substring(0, uri.length() - 1);
         Map params = splitQuery(uri.replace("eporezi://", ""));
         String env = (String)params.get("env");
         String loginKey = (String)params.get("loginKey");
         String xmlUrl = (String)params.get("xmlUrl");
         if (env == null) {
            environment = SmartBox.Environment.UNKNOWN;
         } else {
            byte var6 = -1;
            switch(env.hashCode()) {
            case 100768:
               if (env.equals("eto")) {
                  var6 = 1;
               }
               break;
            case 104612:
               if (env.equals("ito")) {
                  var6 = 2;
               }
               break;
            case 3449687:
               if (env.equals("prod")) {
                  var6 = 0;
               }
            }

            switch(var6) {
            case 0:
               environment = SmartBox.Environment.PRODUCTION;
               baseUrl = SmartBox.PRODUCTION_BASE_URL;
               break;
            case 1:
               environment = SmartBox.Environment.ETO;
               baseUrl = SmartBox.ETO_BASE_URL;
               break;
            case 2:
               environment = SmartBox.Environment.ITO;
               baseUrl = SmartBox.ITO_BASE_URL;
               break;
            default:
               environment = SmartBox.Environment.UNKNOWN;
               baseUrl = null;
            }
         }

         if (environment == SmartBox.Environment.UNKNOWN) {
            JOptionPane.showMessageDialog((Component)null, SmartBox.NOTIFICATION_ENVIRNOMENT_NOT_DETECTED, "SmartBox", 0);
            return;
         }

         if (loginKey != null && loginKey.length() > 0) {
            showLogin(loginKey);
         } else if (xmlUrl != null && xmlUrl.length() > 0) {
            String reqKey = (String)params.get("reqKey");
            String servletUrl = decodeString((String)params.get("servletUrl"));
            String backUrl = decodeString((String)params.get("backUrl"));
            String jmbgAuth = decodeString((String)params.get("jmbgAuth"));
            String pibAuth = decodeString((String)params.get("pibAuth"));
            String taxFormId = decodeString((String)params.get("id"));
            String itemId = decodeString((String)params.get("itemId"));
            showSignXml(reqKey, xmlUrl, servletUrl, backUrl, jmbgAuth, pibAuth, taxFormId, itemId);
         }
      } catch (Exception var12) {
         Utils.logMessage("Error while processing URL: " + var12.getMessage());
         showLogin((String)null);
      }

   }

   private static void setLaf() {
      String os = System.getProperty("os.name");
      os = os.toLowerCase();
      Utils.logMessage("OS info: " + os);

      try {
         if (os.contains("windows")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         } else if (os.contains("linux")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
         } else {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
         }
      } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var2) {
         Utils.logMessage("Error while setting window theme: " + var2.getMessage());
      }

   }

   private static void showLogin(String loginKey) {
      LoginFrame.init(environment, loginKey);
   }

   private static void showSignXml(String reqKey, String xmlUrl, String servletUrl, String backUrl, String jmbgAuth, String pibAuth, String taxFormId, String itemId) {
      SignXmlFrame frame = new SignXmlFrame(environment, baseUrl, reqKey, xmlUrl, servletUrl, backUrl, jmbgAuth, pibAuth, taxFormId, itemId);
      frame.setVisible(true);
      frame.init();
   }

   private static Map splitQuery(String query) throws Exception {
      Map query_pairs = new LinkedHashMap();
      String[] pairs = query.split("&");
      String[] var3 = pairs;
      int var4 = pairs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String pair = var3[var5];
         int idx = pair.indexOf("=");
         query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
      }

      return query_pairs;
   }

   private static String decodeString(String string) {
      try {
         return URLDecoder.decode(string, "UTF-8");
      } catch (Exception var2) {
         return string;
      }
   }

   public static ProxyParams getProxyParams() {
      return proxyParams;
   }

   public static void setProxyParams(ProxyParams proxyParams) {
      SmartBox.proxyParams = proxyParams;
   }

   static {
      environment = SmartBox.Environment.UNKNOWN;
   }

   public static enum Environment {
      PRODUCTION,
      ETO,
      ITO,
      UNKNOWN;
   }
}
