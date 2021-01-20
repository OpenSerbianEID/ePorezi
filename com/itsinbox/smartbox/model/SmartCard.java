package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.utils.Utils;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

public abstract class SmartCard {
   public static final String KEYSTORE_TYPE_MSCAPI = "SunMSCAPI";
   public static final String KEYSTORE_TYPE_PKCS11 = "PKCS11";
   private Card card;
   private CardChannel channel;
   private KeyStore keyStore;
   private Enumeration aliases;

   public abstract String getVendorName();

   public abstract void sendAtr(String var1, String var2);

   public String introduceYourself() {
      byte[] atrBytes = this.card.getATR().getBytes();
      return Utils.bytes2HexString(atrBytes);
   }

   public void disconnect() throws CardException {
      this.disconnect(false);
   }

   public void disconnect(boolean reset) throws CardException {
      this.card.disconnect(reset);
      this.card = null;
   }

   public Card getCard() {
      return this.card;
   }

   public void setCard(Card card) {
      this.card = card;
   }

   public CardChannel getChannel() {
      return this.channel;
   }

   public void setChannel(CardChannel channel) {
      this.channel = channel;
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   public void setKeyStore(KeyStore keyStore) {
      this.keyStore = keyStore;
   }

   public abstract KeyStore loadKeyStore(char[] var1) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException;

   public Enumeration getAliases() {
      try {
         this.aliases = this.getKeyStore().aliases();
      } catch (KeyStoreException var2) {
         this.aliases = null;
         Utils.logMessage("Error while getting aliases: " + var2.getMessage());
      }

      return this.aliases;
   }

   public X509Certificate getCertificate(String alias) {
      X509Certificate cert;
      if (this.getKeyStore() != null) {
         try {
            cert = (X509Certificate)this.getKeyStore().getCertificate(alias);
         } catch (KeyStoreException var4) {
            cert = null;
            Utils.logMessage("Error while getting certificate: " + var4.getMessage());
         }
      } else {
         cert = null;
      }

      return cert;
   }

   public abstract String getKeyStoreProvider();

   public abstract String getKeyStoreType();
}
