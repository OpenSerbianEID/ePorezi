package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.model.SmartCard;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class SmartCardReader {
   private final CardTerminal terminal;
   private volatile SmartCard anycard;
   private CopyOnWriteArrayList listeners;
   private final Thread listenerThread;

   public SmartCardReader(final CardTerminal terminal) {
      this.terminal = terminal;
      this.listeners = new CopyOnWriteArrayList();
      this.listenerThread = new Thread(new Runnable() {
         public void run() {
            try {
               short timeoutMs = 0;

               while(true) {
                  boolean statusChanged = true;

                  try {
                     if (SmartCardReader.this.anycard == null) {
                        terminal.waitForCardPresent((long)timeoutMs);
                     } else {
                        terminal.waitForCardAbsent((long)timeoutMs);
                     }

                     if (SmartCardReader.this.anycard == null && terminal.isCardPresent()) {
                        SmartCardReader.this.connect();
                     } else if (SmartCardReader.this.anycard != null && !terminal.isCardPresent()) {
                        SmartCardReader.this.disconnect();
                     } else {
                        timeoutMs = 3000;
                        statusChanged = false;
                     }
                  } catch (CardException var4) {
                     SmartCardReader.this.anycard = null;
                     if (terminal.isCardPresent()) {
                        SmartCardReader.this.connect();
                     }
                  }

                  if (statusChanged) {
                     this.notifyListeners();
                  }
               }
            } catch (CardException var5) {
            }
         }

         private void notifyListeners() {
            Iterator var1 = SmartCardReader.this.listeners.iterator();

            while(var1.hasNext()) {
               SmartCardReader.ReaderListener listener = (SmartCardReader.ReaderListener)var1.next();
               SmartCardReader.this.notifyCardListener(listener, false);
            }

         }
      });
      this.listenerThread.start();
   }

   public void addCardListener(SmartCardReader.ReaderListener listener) {
      this.listeners.add(listener);
      this.notifyCardListener(listener, true);
   }

   public boolean removeCardListener(SmartCardReader.ReaderListener listener) {
      return this.listeners.remove(listener);
   }

   private void notifyCardListener(SmartCardReader.ReaderListener listener, boolean inserted_only) {
      if (this.anycard != null) {
         listener.inserted(this.anycard);
      } else if (!inserted_only) {
         listener.removed();
      }

   }

   public void connect() throws CardException {
      Card card = this.terminal.connect("*");
      this.anycard = SmartCardFactory.getInstance().getSmartCard(card);
   }

   public void disconnect() throws CardException {
      this.anycard.disconnect();
      this.anycard = null;
   }

   public interface ReaderListener {
      void inserted(SmartCard var1);

      void removed();
   }
}
