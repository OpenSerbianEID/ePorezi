package com.itsinbox.smartbox.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class WinRegistry {
   public static final int HKEY_CURRENT_USER = -2147483647;
   public static final int HKEY_LOCAL_MACHINE = -2147483646;
   public static final int REG_SUCCESS = 0;
   public static final int REG_NOTFOUND = 2;
   public static final int REG_ACCESSDENIED = 5;
   private static final int KEY_ALL_ACCESS = 983103;
   private static final int KEY_READ = 131097;
   private static Preferences userRoot = Preferences.userRoot();
   private static Preferences systemRoot = Preferences.systemRoot();
   private static Class userClass;
   private static Method regOpenKey;
   private static Method regCloseKey;
   private static Method regQueryValueEx;
   private static Method regEnumValue;
   private static Method regQueryInfoKey;
   private static Method regEnumKeyEx;
   private static Method regCreateKeyEx;
   private static Method regSetValueEx;
   private static Method regDeleteKey;
   private static Method regDeleteValue;

   private WinRegistry() {
   }

   public static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if (hkey == -2147483646) {
         return readString(systemRoot, hkey, key, valueName);
      } else if (hkey == -2147483647) {
         return readString(userRoot, hkey, key, valueName);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static Map readStringValues(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if (hkey == -2147483646) {
         return readStringValues(systemRoot, hkey, key);
      } else if (hkey == -2147483647) {
         return readStringValues(userRoot, hkey, key);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static List readStringSubKeys(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if (hkey == -2147483646) {
         return readStringSubKeys(systemRoot, hkey, key);
      } else if (hkey == -2147483647) {
         return readStringSubKeys(userRoot, hkey, key);
      } else {
         throw new IllegalArgumentException("hkey=" + hkey);
      }
   }

   public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] ret;
      if (hkey == -2147483646) {
         ret = createKey(systemRoot, hkey, key);
         regCloseKey.invoke(systemRoot, new Integer(ret[0]));
      } else {
         if (hkey != -2147483647) {
            throw new IllegalArgumentException("hkey=" + hkey);
         }

         ret = createKey(userRoot, hkey, key);
         regCloseKey.invoke(userRoot, new Integer(ret[0]));
      }

      if (ret[1] != 0) {
         throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
      }
   }

   public static void writeStringValue(int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      if (hkey == -2147483646) {
         writeStringValue(systemRoot, hkey, key, valueName, value);
      } else {
         if (hkey != -2147483647) {
            throw new IllegalArgumentException("hkey=" + hkey);
         }

         writeStringValue(userRoot, hkey, key, valueName, value);
      }

   }

   public static void deleteKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = -1;
      if (hkey == -2147483646) {
         rc = deleteKey(systemRoot, hkey, key);
      } else if (hkey == -2147483647) {
         rc = deleteKey(userRoot, hkey, key);
      }

      if (rc != 0) {
         throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
      }
   }

   public static void deleteValue(int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = -1;
      if (hkey == -2147483646) {
         rc = deleteValue(systemRoot, hkey, key, value);
      } else if (hkey == -2147483647) {
         rc = deleteValue(userRoot, hkey, key, value);
      }

      if (rc != 0) {
         throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
      }
   }

   private static int deleteValue(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])regOpenKey.invoke(root, new Integer(hkey), toCstr(key), new Integer(983103));
      if (handles[1] != 0) {
         return handles[1];
      } else {
         int rc = (Integer)regDeleteValue.invoke(root, new Integer(handles[0]), toCstr(value));
         regCloseKey.invoke(root, new Integer(handles[0]));
         return rc;
      }
   }

   private static int deleteKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int rc = (Integer)regDeleteKey.invoke(root, new Integer(hkey), toCstr(key));
      return rc;
   }

   private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])regOpenKey.invoke(root, new Integer(hkey), toCstr(key), new Integer(131097));
      if (handles[1] != 0) {
         return null;
      } else {
         byte[] valb = (byte[])regQueryValueEx.invoke(root, new Integer(handles[0]), toCstr(value));
         regCloseKey.invoke(root, new Integer(handles[0]));
         return valb != null ? (new String(valb)).trim() : null;
      }
   }

   private static Map readStringValues(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      HashMap results = new HashMap();
      int[] handles = (int[])regOpenKey.invoke(root, new Integer(hkey), toCstr(key), new Integer(131097));
      if (handles[1] != 0) {
         return null;
      } else {
         int[] info = (int[])regQueryInfoKey.invoke(root, new Integer(handles[0]));
         int count = info[0];
         int maxlen = info[3];

         for(int index = 0; index < count; ++index) {
            byte[] name = (byte[])regEnumValue.invoke(root, new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1));
            String value = readString(hkey, key, new String(name));
            results.put((new String(name)).trim(), value);
         }

         regCloseKey.invoke(root, new Integer(handles[0]));
         return results;
      }
   }

   private static List readStringSubKeys(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      List results = new ArrayList();
      int[] handles = (int[])regOpenKey.invoke(root, new Integer(hkey), toCstr(key), new Integer(131097));
      if (handles[1] != 0) {
         return null;
      } else {
         int[] info = (int[])regQueryInfoKey.invoke(root, new Integer(handles[0]));
         int count = info[0];
         int maxlen = info[3];

         for(int index = 0; index < count; ++index) {
            byte[] name = (byte[])regEnumKeyEx.invoke(root, new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1));
            results.add((new String(name)).trim());
         }

         regCloseKey.invoke(root, new Integer(handles[0]));
         return results;
      }
   }

   private static int[] createKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      return (int[])regCreateKeyEx.invoke(root, new Integer(hkey), toCstr(key));
   }

   private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      int[] handles = (int[])regOpenKey.invoke(root, new Integer(hkey), toCstr(key), new Integer(983103));
      regSetValueEx.invoke(root, new Integer(handles[0]), toCstr(valueName), toCstr(value));
      regCloseKey.invoke(root, new Integer(handles[0]));
   }

   private static byte[] toCstr(String str) {
      byte[] result = new byte[str.length() + 1];

      for(int i = 0; i < str.length(); ++i) {
         result[i] = (byte)str.charAt(i);
      }

      result[str.length()] = 0;
      return result;
   }

   static {
      userClass = userRoot.getClass();
      regOpenKey = null;
      regCloseKey = null;
      regQueryValueEx = null;
      regEnumValue = null;
      regQueryInfoKey = null;
      regEnumKeyEx = null;
      regCreateKeyEx = null;
      regSetValueEx = null;
      regDeleteKey = null;
      regDeleteValue = null;

      try {
         regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", Integer.TYPE, byte[].class, Integer.TYPE);
         regOpenKey.setAccessible(true);
         regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", Integer.TYPE);
         regCloseKey.setAccessible(true);
         regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", Integer.TYPE, byte[].class);
         regQueryValueEx.setAccessible(true);
         regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", Integer.TYPE, Integer.TYPE, Integer.TYPE);
         regEnumValue.setAccessible(true);
         regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", Integer.TYPE);
         regQueryInfoKey.setAccessible(true);
         regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", Integer.TYPE, Integer.TYPE, Integer.TYPE);
         regEnumKeyEx.setAccessible(true);
         regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", Integer.TYPE, byte[].class);
         regCreateKeyEx.setAccessible(true);
         regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", Integer.TYPE, byte[].class, byte[].class);
         regSetValueEx.setAccessible(true);
         regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", Integer.TYPE, byte[].class);
         regDeleteValue.setAccessible(true);
         regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", Integer.TYPE, byte[].class);
         regDeleteKey.setAccessible(true);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }
}
