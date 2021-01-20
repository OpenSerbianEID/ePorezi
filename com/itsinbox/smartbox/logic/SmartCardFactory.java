package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.model.AnyCard;
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
      SmartCard ret = new AnyCard();
      Utils.logMessage("CertBody: ANY");
      ret.setCard(card);
      ret.setChannel(card.getBasicChannel());
      return ret;
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
