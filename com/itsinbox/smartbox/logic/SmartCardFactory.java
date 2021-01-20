package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.model.AnyCard;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import java.util.Arrays;
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

   private boolean isKnownATR(byte[] card_atr, byte[][] known_atr) {
      byte[][] var3 = known_atr;
      int var4 = known_atr.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte[] eid_atr = var3[var5];
         if (Arrays.equals(card_atr, eid_atr)) {
            return true;
         }
      }

      return false;
   }
}
