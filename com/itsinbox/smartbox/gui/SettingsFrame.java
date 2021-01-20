package com.itsinbox.smartbox.gui;

import com.itsinbox.smartbox.SmartBox;
import com.itsinbox.smartbox.proxy.ProxyParams;
import com.itsinbox.smartbox.proxy.ProxyType;
import com.itsinbox.smartbox.proxy.ProxyUtils;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;

public class SettingsFrame extends JFrame {
   private ProxyType selectedProxyType;
   private ProxyParams proxyParams = SmartBox.getProxyParams();
   private JRadioButton httpProxyRadioButton;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel4;
   private JRadioButton noProxyRadioButton;
   private ButtonGroup proxyButtonGroup;
   private JTextField proxyHostTextField;
   private JPanel proxyPanel;
   private JTextField proxyPortTextField;
   private JButton saveSettingsButton;
   private JRadioButton socksProxyRadioButton;
   private JRadioButton systemProxyRadioButton;

   public SettingsFrame() {
      this.selectedProxyType = this.proxyParams.getProxyType();
      this.initComponents();
      this.setLocationRelativeTo((Component)null);
      this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/com/itsinbox/smartbox/resources/app.png")));
      this.setDefaultCloseOperation(1);
      this.setTitle(SmartBox.WINDOW_TITLE+SmartBox.VERSION+" - Подешавања");
      this.pack();
      this.readInitState();
   }

   private void initComponents() {
      this.proxyButtonGroup = new ButtonGroup();
      this.proxyPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.proxyHostTextField = new JTextField();
      this.proxyPortTextField = new JTextField();
      this.jLabel4 = new JLabel();
      this.noProxyRadioButton = new JRadioButton();
      this.systemProxyRadioButton = new JRadioButton();
      this.httpProxyRadioButton = new JRadioButton();
      this.socksProxyRadioButton = new JRadioButton();
      this.saveSettingsButton = new JButton();
      this.setDefaultCloseOperation(3);
      this.setResizable(false);
      this.jLabel1.setText("Адреса proxy сервера");
      this.jLabel2.setText("Порт proxy сервера");
      GroupLayout proxyPanelLayout = new GroupLayout(this.proxyPanel);
      this.proxyPanel.setLayout(proxyPanelLayout);
      proxyPanelLayout.setHorizontalGroup(proxyPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.proxyHostTextField).addComponent(this.proxyPortTextField).addComponent(this.jLabel1, -1, -1, 32767).addComponent(this.jLabel2, -1, -1, 32767));
      proxyPanelLayout.setVerticalGroup(proxyPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(proxyPanelLayout.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.proxyHostTextField, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.proxyPortTextField, -2, -1, -2).addContainerGap(-1, 32767)));
      this.jLabel1.getAccessibleContext().setAccessibleName("");
      this.jLabel4.setFont(new Font("Tahoma", 1, 12));
      this.jLabel4.setText("Подешавања proxy сервера");
      this.proxyButtonGroup.add(this.noProxyRadioButton);
      this.noProxyRadioButton.setSelected(true);
      this.noProxyRadioButton.setText("Не користи proxy сервер");
      this.noProxyRadioButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SettingsFrame.this.noProxyRadioButtonActionPerformed(evt);
         }
      });
      this.proxyButtonGroup.add(this.systemProxyRadioButton);
      this.systemProxyRadioButton.setText("Користи системски proxy сервер");
      this.systemProxyRadioButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SettingsFrame.this.systemProxyRadioButtonActionPerformed(evt);
         }
      });
      this.proxyButtonGroup.add(this.httpProxyRadioButton);
      this.httpProxyRadioButton.setText("Користи HTTP/HTTPS proxy сервер");
      this.httpProxyRadioButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SettingsFrame.this.httpProxyRadioButtonActionPerformed(evt);
         }
      });
      this.proxyButtonGroup.add(this.socksProxyRadioButton);
      this.socksProxyRadioButton.setText("Користи SOCSK proxy сервер");
      this.socksProxyRadioButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SettingsFrame.this.socksProxyRadioButtonActionPerformed(evt);
         }
      });
      this.saveSettingsButton.setText("Примени");
      this.saveSettingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SettingsFrame.this.saveSettingsButtonActionPerformed(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.proxyPanel, -1, -1, 32767).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.socksProxyRadioButton).addComponent(this.httpProxyRadioButton).addComponent(this.systemProxyRadioButton).addComponent(this.noProxyRadioButton).addComponent(this.jLabel4)).addGap(0, 102, 32767))).addContainerGap()).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.saveSettingsButton).addGap(121, 121, 121)));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel4).addGap(13, 13, 13).addComponent(this.noProxyRadioButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.systemProxyRadioButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.httpProxyRadioButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.socksProxyRadioButton).addPreferredGap(ComponentPlacement.RELATED, 7, 32767).addComponent(this.proxyPanel, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.saveSettingsButton).addContainerGap()));
      this.saveSettingsButton.getAccessibleContext().setAccessibleName("Сачувај");
      this.pack();
   }

   private void systemProxyRadioButtonActionPerformed(ActionEvent evt) {
      this.proxyPanel.setVisible(false);
      this.selectedProxyType = ProxyType.SYSTEM_PROXY;
      this.pack();
   }

   private void noProxyRadioButtonActionPerformed(ActionEvent evt) {
      this.proxyPanel.setVisible(false);
      this.selectedProxyType = ProxyType.NO_PROXY;
      this.pack();
   }

   private void httpProxyRadioButtonActionPerformed(ActionEvent evt) {
      this.proxyPanel.setVisible(true);
      this.selectedProxyType = ProxyType.HTTP_PROXY;
      this.pack();
   }

   private void socksProxyRadioButtonActionPerformed(ActionEvent evt) {
      this.proxyPanel.setVisible(true);
      this.selectedProxyType = ProxyType.SOCKS_PROXY;
      this.pack();
   }

   private void saveSettingsButtonActionPerformed(ActionEvent evt) {
      if (this.selectedProxyType != null) {
         switch(this.selectedProxyType) {
         case NO_PROXY:
            ProxyUtils.setNoProxy(true);
            this.proxyParams = new ProxyParams(ProxyType.NO_PROXY);
            break;
         case SYSTEM_PROXY:
            ProxyUtils.setSystemProxy(true);
            this.proxyParams = new ProxyParams(ProxyType.SYSTEM_PROXY);
            break;
         case HTTP_PROXY:
            ProxyUtils.setHttpProxy(this.proxyHostTextField.getText(), this.proxyPortTextField.getText(), true);
            this.proxyParams = new ProxyParams(ProxyType.HTTP_PROXY, this.proxyHostTextField.getText(), this.proxyPortTextField.getText());
            break;
         case SOCKS_PROXY:
            ProxyUtils.setSocksProxy(this.proxyHostTextField.getText(), this.proxyPortTextField.getText(), true);
            this.proxyParams = new ProxyParams(ProxyType.SOCKS_PROXY, this.proxyHostTextField.getText(), this.proxyPortTextField.getText());
         }

         SmartBox.setProxyParams(this.proxyParams);
      }

      this.dispose();
   }

   public static void main(String[] args) {
      try {
         LookAndFeelInfo[] var1 = UIManager.getInstalledLookAndFeels();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            LookAndFeelInfo info = var1[var3];
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException var5) {
         Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, (String)null, var5);
      } catch (InstantiationException var6) {
         Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, (String)null, var6);
      } catch (IllegalAccessException var7) {
         Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, (String)null, var7);
      } catch (UnsupportedLookAndFeelException var8) {
         Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, (String)null, var8);
      }

      EventQueue.invokeLater(new Runnable() {
         public void run() {
            (new SettingsFrame()).setVisible(true);
         }
      });
   }

   public static void init() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            (new SettingsFrame()).setVisible(true);
         }
      });
   }

   private void readInitState() {
      if (this.proxyParams != null && this.proxyParams.getProxyType() != null) {
         switch(this.proxyParams.getProxyType()) {
         case NO_PROXY:
            this.noProxyRadioButton.setSelected(true);
            this.proxyPanel.setVisible(false);
            break;
         case SYSTEM_PROXY:
            this.systemProxyRadioButton.setSelected(true);
            this.proxyPanel.setVisible(false);
            break;
         case HTTP_PROXY:
            this.httpProxyRadioButton.setSelected(true);
            this.proxyPanel.setVisible(true);
            if (this.proxyParams.getProxyHost() != null) {
               this.proxyHostTextField.setText(this.proxyParams.getProxyHost());
            }

            if (this.proxyParams.getProxyPort() != null) {
               this.proxyPortTextField.setText(this.proxyParams.getProxyPort());
            }
            break;
         case SOCKS_PROXY:
            this.socksProxyRadioButton.setSelected(true);
            this.proxyPanel.setVisible(true);
            if (this.proxyParams.getProxyHost() != null) {
               this.proxyHostTextField.setText(this.proxyParams.getProxyHost());
            }

            if (this.proxyParams.getProxyPort() != null) {
               this.proxyPortTextField.setText(this.proxyParams.getProxyPort());
            }
         }

         this.pack();
      }

   }
}
