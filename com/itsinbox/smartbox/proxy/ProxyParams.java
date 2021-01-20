package com.itsinbox.smartbox.proxy;

public class ProxyParams {
   private final ProxyType proxyType;
   private final String proxyHost;
   private final String proxyPort;

   public ProxyParams(ProxyType proxyType) {
      this.proxyType = proxyType;
      this.proxyHost = null;
      this.proxyPort = null;
   }

   public ProxyParams(ProxyType proxyType, String proxyHost, String proxyPort) {
      this.proxyType = proxyType;
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
   }

   public ProxyType getProxyType() {
      return this.proxyType;
   }

   public String getProxyHost() {
      return this.proxyHost;
   }

   public String getProxyPort() {
      return this.proxyPort;
   }
}
