package com.itsinbox.smartbox.logic;

import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.KeySelector.Purpose;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class KeyValueKeySelector extends KeySelector {
   public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
      if (keyInfo == null) {
         throw new KeySelectorException("KeyInfo object is null!");
      } else {
         SignatureMethod sm = (SignatureMethod)method;
         List list = keyInfo.getContent();

         for(int i = 0; i < list.size(); ++i) {
            XMLStructure xmlStructure = (XMLStructure)list.get(i);
            if (xmlStructure instanceof KeyValue) {
               PublicKey pk = null;

               try {
                  pk = ((KeyValue)xmlStructure).getPublicKey();
               } catch (KeyException var11) {
                  throw new KeySelectorException(var11);
               }

               if (!this.algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                  throw new KeySelectorException("Signature Algorithm in not compatible with key algorithm!");
               }

               if (purpose != Purpose.VERIFY) {
                  throw new KeySelectorException("The public key is for validation only in XML signature!");
               }

               if (!(context instanceof XMLValidateContext)) {
                  throw new KeySelectorException("The context must be for validation!");
               }

               return new SimpleKeySelectorResult(pk);
            }
         }

         throw new KeySelectorException("No KeyValue element found in KeyInfo!");
      }
   }

   private boolean algEquals(String sigAlg, String keyAlg) {
      if (keyAlg.equalsIgnoreCase("DSA") && sigAlg.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
         return true;
      } else {
         return keyAlg.equalsIgnoreCase("RSA") && sigAlg.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
      }
   }
}
