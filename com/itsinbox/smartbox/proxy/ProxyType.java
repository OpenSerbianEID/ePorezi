package com.itsinbox.smartbox.proxy;

public enum ProxyType {
   NO_PROXY(0),
   SYSTEM_PROXY(1),
   HTTP_PROXY(2),
   SOCKS_PROXY(3);

   private final int value;
   private static final ProxyType[] allValues = values();

   private ProxyType(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static ProxyType fromOrdinal(int n) {
      return allValues[n];
   }
}
