package com.itsinbox.smartbox.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;

public class LogFrame extends JFrame {
   private String log;
   private JScrollPane jScrollPane1;
   private JButton logCopyButton;
   private JTextArea logTextArea;

   public LogFrame() {
      this.initComponents();
      this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/com/itsinbox/smartbox/resources/app.png")));
      this.setDefaultCloseOperation(1);
      this.readLog();
      this.logCopyButton.setOpaque(true);
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.logTextArea = new JTextArea();
      this.logCopyButton = new JButton();
      this.setDefaultCloseOperation(3);
      this.logTextArea.setEditable(false);
      this.logTextArea.setColumns(20);
      this.logTextArea.setLineWrap(true);
      this.logTextArea.setRows(5);
      this.logTextArea.setWrapStyleWord(true);
      this.jScrollPane1.setViewportView(this.logTextArea);
      this.logCopyButton.setBackground(new Color(210, 44, 52));
      this.logCopyButton.setFont(new Font("Tahoma", 0, 14));
      this.logCopyButton.setForeground(new Color(255, 255, 255));
      this.logCopyButton.setText("Копирај");
      this.logCopyButton.setBorder((Border)null);
      this.logCopyButton.setBorderPainted(false);
      this.logCopyButton.setContentAreaFilled(false);
      this.logCopyButton.setMaximumSize(new Dimension(125, 30));
      this.logCopyButton.setMinimumSize(new Dimension(125, 30));
      this.logCopyButton.setPreferredSize(new Dimension(125, 30));
      this.logCopyButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LogFrame.this.logCopyButtonActionPerformed(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 480, 32767).addGroup(layout.createSequentialGroup().addGap(0, 0, 32767).addComponent(this.logCopyButton, -2, 125, -2))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 337, 32767).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.logCopyButton, -1, -1, -2).addContainerGap()));
      this.pack();
   }

   private void logCopyButtonActionPerformed(ActionEvent evt) {
      if (this.log != null) {
         StringSelection stringSelection = new StringSelection(this.log);
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(stringSelection, (ClipboardOwner)null);
      }

   }

   public static void init() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            (new LogFrame()).setVisible(true);
         }
      });
   }

   private void readLog() {
      String logPath = System.getProperty("user.home") + File.separator + "eporezi.log";

      try {
         this.log = new String(Files.readAllBytes(Paths.get(logPath)));
         if (this.log != null) {
            this.logTextArea.setText(this.log);
         }
      } catch (IOException var3) {
         Logger.getLogger(LogFrame.class.getName()).log(Level.SEVERE, (String)null, var3);
      }

   }
}
