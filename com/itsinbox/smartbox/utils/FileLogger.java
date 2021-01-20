package com.itsinbox.smartbox.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLogger {
   private String logPath;
   private BufferedWriter writer;

   public FileLogger() {
      this.logPath = System.getProperty("user.home") + File.separator + "eporezi.log";

      try {
         this.writer = new BufferedWriter(new FileWriter(this.logPath));
      } catch (IOException var2) {
         Logger.getLogger(FileLogger.class.getName()).log(Level.SEVERE, (String)null, var2);
      }

   }

   public void writeToLog(String message) {
      try {
         this.writer.append(message);
         this.writer.newLine();
         this.writer.flush();
      } catch (IOException var3) {
         Logger.getLogger(FileLogger.class.getName()).log(Level.SEVERE, (String)null, var3);
      }

   }
}
