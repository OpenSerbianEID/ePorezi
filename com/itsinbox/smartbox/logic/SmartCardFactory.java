package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.model.AnyCard;
import com.itsinbox.smartbox.model.PKCS11CardEdge;
import com.itsinbox.smartbox.model.PKCS11SafeSign;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import javax.smartcardio.Card;

public class SmartCardFactory {
   private static SmartCardFactory instance;

   public static SmartCardFactory getInstance() {
      if (instance == null) {
         instance = new SmartCardFactory();
      }

      return instance;
   }

   public SmartCard getSmartCard(Card card) {
      int osFamily = SmartCard.getOsFamily();
      SmartCard smartCard = null;
      if(osFamily == 1) {
         smartCard = new AnyCard();
         Utils.logMessage("CertBody: ANY");
      }
      else {
         String attr = Utils.bytes2HexString(card.getATR().getBytes());
         if(this.isKnownATR(attr, PKCS11CardEdge.KNOWN_ATRS)) {
            smartCard = new PKCS11CardEdge();
            Utils.logMessage("CertBody: MUP/PKS (CardEdge PKCS11)");
         }
         else if(this.isKnownATR(attr, PKCS11SafeSign.KNOWN_ATRS))
         {
            smartCard = new PKCS11SafeSign();
            Utils.logMessage("CertBody: Posta (SafeSign PKCS11)");
         }
      }
      if(smartCard != null) {
         smartCard.setCard(card);
         smartCard.setChannel(card.getBasicChannel());
         return smartCard;
      }
      return null;
   }

   private boolean isKnownATR(String card_atr, String[] known_atr) {
      for (String eid_atr : known_atr) {
         if (card_atr.equals(eid_atr)) {
            return true;
         }
      }
      return false;
   }
}
