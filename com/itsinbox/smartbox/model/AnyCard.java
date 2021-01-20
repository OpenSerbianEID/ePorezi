package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.utils.Utils;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.CertificateException;

public class AnyCard extends SmartCard {
   private static final String KEYSTORE_TYPE = "Windows-MY";
   private static final String KEYSTORE_PROVIDER = "SunMSCAPI";
   public static final byte[][] KNOWN_EID_ATRS = new byte[0][];

   public String getVendorName() {
      return "";
   }

   public KeyStore loadKeyStore(char[] pin) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
      Utils.logMessage("SunMSCAPI");
      Provider provider = Security.getProvider("SunMSCAPI");
      KeyStore store = KeyStore.getInstance("Windows-MY", provider);
      store.load((LoadStoreParameter)null);
      SmartCardLogic._fixAliases(store);
      return store;
   }

   public void sendAtr(String vendorName, String issuerCn) {
   }

   public String getKeyStoreProvider() {
      return "SunMSCAPI";
   }

   public String getKeyStoreType() {
      return "Windows-MY";
   }
}
