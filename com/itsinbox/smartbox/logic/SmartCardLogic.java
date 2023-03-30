package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.utils.Utils;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class SmartCardLogic {
   public static String findAlias(KeyStore keyStore) {
      String ret = null;
      ArrayList<String> aliasList = new ArrayList<String>();

      try {
         Enumeration<String> aliases = keyStore.aliases();
         Utils.logMessage("Aliases: ");

         while(aliases.hasMoreElements()) {
            String aliasKey = (String)aliases.nextElement();

            if (keyStore.isKeyEntry(aliasKey)) {
               Utils.logMessage("Alias: " + aliasKey);
               aliasList.add(aliasKey);
            }
         }
      } catch (KeyStoreException var5) {
         Utils.logMessage("Error while finding alias: " + var5.getMessage());
      }

      if (!aliasList.isEmpty()) {
         ret = findCorrectAlias(keyStore, aliasList);
      }

      return ret;
   }

   public static String extractPersonalId(String dn) {
      String id = "";
      if (dn != null) {
         Pattern pat = Pattern.compile("[0-9]{13}");
         Matcher matcher = pat.matcher(dn);
         if (matcher.find()) {
            Utils.logMessage("PERSONAL ID: " + matcher.group(0));
            id = matcher.group(0);
         }
      }

      return id;
   }

   public static String findPersonalId(KeyStore keyStore, String alias) {
      String personalId = "";

      try {
         Certificate[] chain = keyStore.getCertificateChain(alias);
         if (chain.length > 0) {
            X509Certificate firstInChain = (X509Certificate)chain[0];
            String dn = firstInChain.getSubjectX500Principal().toString();
            Utils.logMessage("DN: " + dn);

            try {
               LdapName ldapDN = new LdapName(dn);
               Iterator var7 = ldapDN.getRdns().iterator();

               while(var7.hasNext()) {
                  Rdn rdn = (Rdn)var7.next();
                  String type = rdn.getType();
                  if (type.equals("CN")) {
                     personalId = extractPersonalId(rdn.getValue().toString());
                  } else if (personalId.length() != 13 && type.equals("SERIALNUMBER")) {
                     String[] rdnValues = rdn.toString().split("=");
                     String[] var11 = rdnValues;
                     int var12 = rdnValues.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        String rdnValue = var11[var13];
                        if (rdnValue.toUpperCase().startsWith("PNORS-")) {
                           personalId = extractPersonalId(rdnValue);
                        }
                     }
                  }

                  if (personalId.length() == 13) {
                     break;
                  }
               }
            } catch (InvalidNameException var15) {
               Utils.logMessage("Invalid name found while finding personalId: " + var15.getMessage());
            }
         }
      } catch (KeyStoreException var16) {
         Utils.logMessage("Keystore error while finding personalId: " + var16.getMessage());
      }

      return personalId;
   }

   public static String findCorrectAlias(KeyStore keyStore, List aliasList) {
      String ret = null;
      Iterator var3 = aliasList.iterator();

      while(var3.hasNext()) {
         String alias = (String)var3.next();
         String personalId = "";
         boolean ku = false;

         try {
            personalId = findPersonalId(keyStore, alias);
            Certificate[] chain = keyStore.getCertificateChain(alias);

            for(int i = 0; i < chain.length; ++i) {
               X509Certificate chainMember = (X509Certificate)chain[i];
               boolean[] keyUsage = chainMember.getKeyUsage();
               if (keyUsage[0]) {
                  Utils.logMessage("digitalSignature");
               }

               if (keyUsage[1]) {
                  Utils.logMessage("nonRepudiation");
               }

               if (keyUsage[2]) {
                  Utils.logMessage("keyEncypherment");
               }

               if (keyUsage[3]) {
                  Utils.logMessage("dataEncypherment");
               }

               if (keyUsage[4]) {
                  Utils.logMessage("keyAgreement");
               }

               if (keyUsage[5]) {
                  Utils.logMessage("keyCertSign");
               }

               if (keyUsage[6]) {
                  Utils.logMessage("cRLSign");
               }

               if (keyUsage[7]) {
                  Utils.logMessage("encipherOnly");
               }

               if (keyUsage[8]) {
                  Utils.logMessage("decipherOnly");
               }

               ku = ku || keyUsage[0] || keyUsage[1];
            }
         } catch (KeyStoreException var11) {
            Utils.logMessage("Error while finding correct alias: " + var11.getMessage());
         }

         if (personalId.length() == 13 && ku) {
            ret = alias;
         }
      }

      return ret;
   }

   public static void _fixAliases(KeyStore keyStore) {
      try {
         Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
         field.setAccessible(true);
         KeyStoreSpi keyStoreVeritable = (KeyStoreSpi)field.get(keyStore);
         if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
            field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
            field.setAccessible(true);
            Collection entries = (Collection)field.get(keyStoreVeritable);
            Iterator var7 = entries.iterator();

            while(var7.hasNext()) {
               Object entry = var7.next();
               field = entry.getClass().getDeclaredField("certChain");
               field.setAccessible(true);
               X509Certificate[] certificates = (X509Certificate[])field.get(entry);
               String hashCode = Integer.toString(certificates[0].hashCode());
               field = entry.getClass().getDeclaredField("alias");
               field.setAccessible(true);
               String alias = (String)field.get(entry);
               if (!alias.equals(hashCode)) {
                  field.set(entry, alias.concat(" - ").concat(hashCode));
               }
            }
         }
      } catch (Exception var9) {
         Utils.logMessage("Error while fixing alias: " + var9.getMessage());
      }

   }
}
