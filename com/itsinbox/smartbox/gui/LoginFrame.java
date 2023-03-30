package com.itsinbox.smartbox.gui;

import com.itsinbox.smartbox.SmartBox;
import com.itsinbox.smartbox.logic.SigningLogic;
import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.logic.SmartCardReader;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.HttpUtils;
import com.itsinbox.smartbox.utils.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;

public class LoginFrame extends JFrame implements SmartCardReader.ReaderListener, ActionListener {
   private String loginKey;
   private final boolean newTab;
   private SmartCard card = null;
   private String personalId;
   private String firstName;
   private String lastName;
   private String chosenAlias;
   private String xml;
   private String signatureStr;
   private CardTerminal terminal;
   private String newAppVersion;
   private final SmartBox.Environment environment;
   private boolean productionClicked = false;
   private ButtonGroup certBodyGroup;
   private JLabel certStatusLabel;
   private JPanel dividerPanel;
   private JButton headerButton;
   private JLabel jLabel10;
   private JLabel jLabel4;
   private JLabel jLabel9;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JLabel loadingAnimation;
   private JButton logButton;
   private JButton loginProductionButton;
   private JButton loginTestButton;
   private JPanel panelAuthorization;
   private JLabel postaEporeziLabel;
   private JButton proxyButton;
   private JLabel versionInfoLabel;

   public LoginFrame(SmartBox.Environment environment, String loginKey) {
      this.environment = environment;
      this.loginKey = loginKey;
      this.newTab = loginKey == null;
      this.initComponents();
      this.initVisuals();
      this.initTerminal();
      this.initCard();
      this.updateCheck();
   }

   private void initComponents() {
      this.certBodyGroup = new ButtonGroup();
      this.panelAuthorization = new JPanel();
      this.loginProductionButton = new JButton();
      this.loginTestButton = new JButton();
      this.jLabel9 = new JLabel();
      this.postaEporeziLabel = new JLabel();
      this.jLabel10 = new JLabel();
      this.headerButton = new JButton();
      this.jPanel1 = new JPanel();
      this.certStatusLabel = new JLabel();
      this.dividerPanel = new JPanel();
      this.loadingAnimation = new JLabel();
      this.jPanel2 = new JPanel();
      this.logButton = new JButton();
      this.jLabel4 = new JLabel();
      this.proxyButton = new JButton();
      this.versionInfoLabel = new JLabel();
      this.setDefaultCloseOperation(3);
      this.setResizable(false);
      this.panelAuthorization.setMaximumSize((Dimension)null);
      this.loginProductionButton.setBackground(new Color(210, 44, 52));
      this.loginProductionButton.setFont(new Font("Tahoma", 0, 18));
      this.loginProductionButton.setForeground(new Color(255, 255, 255));
      this.loginProductionButton.setText("ПОДНЕСИ ПРИЈАВУ");
      this.loginProductionButton.setBorder((Border)null);
      this.loginProductionButton.setBorderPainted(false);
      this.loginProductionButton.setContentAreaFilled(false);
      this.loginProductionButton.setMaximumSize(new Dimension(120, 50));
      this.loginProductionButton.setMinimumSize(new Dimension(120, 50));
      this.loginProductionButton.setOpaque(true);
      this.loginProductionButton.setPreferredSize(new Dimension(120, 50));
      this.loginProductionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LoginFrame.this.loginProductionButtonActionPerformed(evt);
         }
      });
      this.loginTestButton.setBackground(new Color(33, 48, 84));
      this.loginTestButton.setFont(new Font("Tahoma", 0, 18));
      this.loginTestButton.setForeground(new Color(255, 255, 255));
      this.loginTestButton.setText("Тестно окружење");
      this.loginTestButton.setBorder((Border)null);
      this.loginTestButton.setBorderPainted(false);
      this.loginTestButton.setContentAreaFilled(false);
      this.loginTestButton.setMaximumSize(new Dimension(120, 50));
      this.loginTestButton.setMinimumSize(new Dimension(120, 50));
      this.loginTestButton.setOpaque(true);
      this.loginTestButton.setPreferredSize(new Dimension(120, 50));
      this.loginTestButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LoginFrame.this.loginTestButtonActionPerformed(evt);
         }
      });
      this.jLabel9.setFont(new Font("Arial", 0, 12));
      this.jLabel9.setText("<html>\"Тестно окружење\" служи за тестирање и проверу исправности електронских пореских пријава. Пријаве које су поднете путем \"Тестног окружења\" не стварају пореску обавезу.</html>");
      this.jLabel9.setVerticalAlignment(1);
      this.postaEporeziLabel.setFont(new Font("Arial", 0, 12));
      this.postaEporeziLabel.setText("<html>Сва питања, сугестије и примедбе на рад портала еПорези можете послати електронском поштом на адресу <a href=\"mailto:eporezi@purs.gov.rs\">eporezi@purs.gov.rs</a></html>");
      this.postaEporeziLabel.setVerticalAlignment(1);
      this.postaEporeziLabel.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            LoginFrame.this.postaEporeziLabelMouseClicked(evt);
         }
      });
      this.jLabel10.setFont(new Font("Arial", 1, 14));
      this.jLabel10.setText("Пријавите се на");
      this.headerButton.setIcon(new ImageIcon(this.getClass().getResource("/com/itsinbox/smartbox/resources/header.png")));
      this.headerButton.setBorder((Border)null);
      this.headerButton.setBorderPainted(false);
      this.headerButton.setContentAreaFilled(false);
      this.headerButton.setFocusPainted(false);
      this.headerButton.setFocusable(false);
      this.jPanel1.setBackground(new Color(228, 228, 228));
      this.certStatusLabel.setFont(new Font("Arial", 0, 12));
      this.certStatusLabel.setText("Статус сертификата.");
      this.certStatusLabel.setVerticalAlignment(1);
      GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.certStatusLabel, -1, -1, 32767).addContainerGap()));
      jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.certStatusLabel, -1, 51, 32767).addContainerGap()));
      this.dividerPanel.setBackground(new Color(211, 211, 211));
      this.dividerPanel.setMaximumSize(new Dimension(32767, 1));
      this.dividerPanel.setPreferredSize(new Dimension(913, 1));
      GroupLayout dividerPanelLayout = new GroupLayout(this.dividerPanel);
      this.dividerPanel.setLayout(dividerPanelLayout);
      dividerPanelLayout.setHorizontalGroup(dividerPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      dividerPanelLayout.setVerticalGroup(dividerPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
      this.logButton.setIcon(new ImageIcon(this.getClass().getResource("/com/itsinbox/smartbox/resources/log_36.png")));
      this.logButton.setToolTipText("Лог");
      this.logButton.setBorder((Border)null);
      this.logButton.setBorderPainted(false);
      this.logButton.setContentAreaFilled(false);
      this.logButton.setFocusPainted(false);
      this.logButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LoginFrame.this.logButtonActionPerformed(evt);
         }
      });
      this.jLabel4.setFont(new Font("Arial", 1, 14));
      this.jLabel4.setHorizontalAlignment(2);
      this.jLabel4.setText("<html>Пажња! Три неуспешна покушаја уноса ПИН кода блокираће вашу картицу.</html>");
      this.proxyButton.setIcon(new ImageIcon(this.getClass().getResource("/com/itsinbox/smartbox/resources/settings.png")));
      this.proxyButton.setToolTipText("Подешавања");
      this.proxyButton.setBorder((Border)null);
      this.proxyButton.setBorderPainted(false);
      this.proxyButton.setContentAreaFilled(false);
      this.proxyButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LoginFrame.this.proxyButtonActionPerformed(evt);
         }
      });
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel4, -1, 783, 32767).addGap(29, 29, 29).addComponent(this.proxyButton, -2, 50, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.logButton)));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.logButton, -1, -1, 32767).addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap(-1, 32767).addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4, -2, -1, -2).addComponent(this.proxyButton)).addContainerGap()));
      GroupLayout panelAuthorizationLayout = new GroupLayout(this.panelAuthorization);
      this.panelAuthorization.setLayout(panelAuthorizationLayout);
      panelAuthorizationLayout.setHorizontalGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addGroup(panelAuthorizationLayout.createSequentialGroup().addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addGroup(panelAuthorizationLayout.createSequentialGroup().addContainerGap().addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addComponent(this.dividerPanel, Alignment.TRAILING, -1, 901, 32767).addComponent(this.headerButton, Alignment.TRAILING, -2, 901, 32767).addGroup(Alignment.TRAILING, panelAuthorizationLayout.createSequentialGroup().addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.TRAILING, false).addComponent(this.postaEporeziLabel, Alignment.LEADING, -1, 550, 32767).addComponent(this.loginProductionButton, Alignment.LEADING, -1, -1, 32767)).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, panelAuthorizationLayout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jLabel10, -1, -1, 32767).addComponent(this.loginTestButton, Alignment.TRAILING, -1, 314, 32767)).addComponent(this.jLabel9, Alignment.TRAILING, -2, 316, -2))).addComponent(this.jPanel1, -1, -1, 32767))).addGroup(panelAuthorizationLayout.createSequentialGroup().addGap(446, 446, 446).addComponent(this.loadingAnimation).addGap(0, 0, 32767)).addComponent(this.jPanel2, Alignment.TRAILING, -1, -1, 32767)).addContainerGap()));
      panelAuthorizationLayout.setVerticalGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addGroup(panelAuthorizationLayout.createSequentialGroup().addComponent(this.headerButton).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel2, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED, -1, 32767).addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.loadingAnimation).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.dividerPanel, -2, -1, -2).addGap(18, 18, 18).addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING, false).addGroup(panelAuthorizationLayout.createSequentialGroup().addComponent(this.jLabel10).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.loginTestButton, -1, -1, 32767)).addComponent(this.loginProductionButton, -1, -1, 32767)).addGap(12, 12, 12).addGroup(panelAuthorizationLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel9, -2, 78, -2).addComponent(this.postaEporeziLabel, -2, 34, -2))));
      this.versionInfoLabel.setFont(new Font("Arial", 1, 14));
      this.versionInfoLabel.setForeground(new Color(255, 0, 51));
      this.versionInfoLabel.setText("Нова верзија апликације је доступна. Кликните овде за преузимање.");
      this.versionInfoLabel.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            LoginFrame.this.versionInfoLabelMouseClicked(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.versionInfoLabel, -1, -1, 32767)).addGroup(layout.createSequentialGroup().addComponent(this.panelAuthorization, -2, -1, -2).addGap(0, 0, 32767))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.panelAuthorization, -1, -1, 32767).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.versionInfoLabel).addGap(21, 21, 21)));
      this.pack();
   }

   private void loginTestButtonActionPerformed(ActionEvent evt) {
      this.productionClicked = false;
      switch(this.environment) {
      case ITO:
         this.performLogin(SmartBox.ITO_BASE_URL);
         break;
      default:
         this.performLogin(SmartBox.ETO_BASE_URL);
      }

   }

   private void loginProductionButtonActionPerformed(ActionEvent evt) {
      this.productionClicked = true;
      this.performLogin(SmartBox.PRODUCTION_BASE_URL);
   }

   private void postaEporeziLabelMouseClicked(MouseEvent evt) {
      try {
         Utils.openURL("mailto:eporezi@purs.gov.rs");
      } catch (IOException var3) {
         Utils.logMessage("Error while processing mailto link: " + var3.getMessage());
      }

   }

   private void versionInfoLabelMouseClicked(MouseEvent evt) {
      try {
         Utils.openURL(SmartBox.APP_DOWNLOAD_BASE_URL + this.newAppVersion + ".exe");
      } catch (IOException var3) {
         Utils.logMessage("Error while opening URL: " + var3.getMessage());
      }

   }

   private void logButtonActionPerformed(ActionEvent evt) {
      LogFrame.init();
   }

   private void proxyButtonActionPerformed(ActionEvent evt) {
      SettingsFrame.init();
   }

   public static void init(final SmartBox.Environment environment, final String loginKey) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            SmartBox.loginFrame = new LoginFrame(environment, loginKey);
            SmartBox.loginFrame.setVisible(true);
         }
      });
   }

   private void initVisuals() {
      this.setTitle(SmartBox.WINDOW_TITLE + SmartBox.VERSION);
      this.setLocationRelativeTo((Component)null);
      this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/com/itsinbox/smartbox/resources/app.png")));
      this.versionInfoLabel.setVisible(false);
      this.pack();
   }

   private void initTerminal() {
      try {
         TerminalFactory factory = TerminalFactory.getDefault();
         List terminals = factory.terminals().list();
         this.terminal = (CardTerminal)terminals.get(0);
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CARD);
         this.disableLoginButtons();
      } catch (Exception var3) {
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_READER);
         this.disableLoginButtons();
      }

   }

   private void initCard() {
      if (this.terminal != null) {
         SmartCardReader reader = new SmartCardReader(this.terminal);
         reader.addCardListener(this);
      } else {
         this.initTerminalCheck();
      }

   }

   private void initTerminalCheck() {
      final Timer terminalCheckTimer = new Timer();
      TimerTask terminalCheckTask = new TimerTask() {
         public void run() {
            LoginFrame.this.initTerminal();
            if (LoginFrame.this.terminal != null) {
               terminalCheckTimer.cancel();
               terminalCheckTimer.purge();
               LoginFrame.this.initCard();
            }

         }
      };
      terminalCheckTimer.schedule(terminalCheckTask, 1000L, 1000L);
   }

   private void performLogin(final String baseUrl) {
      if (this.terminal == null) {
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_READER);
         JOptionPane.showMessageDialog((Component)null, SmartBox.NOTIFICATION_NO_READER, "SmartBox", 0);
      } else if (this.card == null) {
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CARD);
         JOptionPane.showMessageDialog((Component)null, SmartBox.NOTIFICATION_NO_CARD, "SmartBox", 0);
      } else {
         if (this.productionClicked) {
            this.certStatusLabel.setText(SmartBox.NOTIFICATION_LOGGING_IN);
         } else {
            this.certStatusLabel.setText(SmartBox.NOTIFICATION_LOGGING_IN_TEST);
         }

         this.disableLoginButtons();
         this.startLoadingAnimation();
         this.getContentPane().repaint();
         (new Thread() {
            public void run() {
               KeyStore keyStore = LoginFrame.this.loadKeyStore();
               if (keyStore != null) {
                  LoginFrame.this.personalization();
                  if (LoginFrame.this.checkCert(baseUrl, keyStore)) {
                     Utils.logMessage("IM SENDING: " + LoginFrame.this.signatureStr);
                     HashMap params = new HashMap();
                     params.put("jmbg", LoginFrame.this.personalId);
                     params.put("firstName", LoginFrame.this.firstName);
                     params.put("lastName", LoginFrame.this.lastName);
                     params.put("pinOK", String.valueOf(true));
                     params.put("token", LoginFrame.this.signatureStr);
                     params.put("chosenAlias", LoginFrame.this.chosenAlias);
                     if (LoginFrame.this.newTab) {
                        LoginFrame.this.loginKey = LoginFrame.this.generateLoginKey();
                     }

                     params.put("loginKey", LoginFrame.this.loginKey);

                     try {
                        HttpUtils.sendHttpPost(baseUrl + "/sw4i/smartBoxLogin", params);
                        if (LoginFrame.this.newTab) {
                           Utils.openURL(baseUrl + "/user/login2.html?loginKey=" + LoginFrame.this.loginKey);
                        }

                        LoginFrame.this.card.disconnect();
                        Runtime.getRuntime().exit(0);
                     } catch (Exception var4) {
                        Utils.logMessage("Error in communication with the server while logging in: " + var4.getMessage());
                        LoginFrame.this.enableLoginButtons();
                        LoginFrame.this.certStatusLabel.setText(SmartBox.NOTIFICATION_SERVER_ERROR);
                     }
                  } else {
                     Utils.logMessage("Error: Something wrong with the certificate!");
                  }
               }

               LoginFrame.this.enableLoginButtons();
               LoginFrame.this.stopLoadingAnimation();
            }
         }).start();
      }
   }

   private void personalization() {
      String validAlias = SmartCardLogic.findAlias(this.card.getKeyStore());
      if (validAlias != null) {
         this.getCertificateInfo(validAlias);
      } else {
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CERT_DATA + " #1");
      }

   }

   private String getCertificateInfo(String alias) {
      StringBuilder text = new StringBuilder();

      try {
         if (this.card != null) {
            StringBuilder textAreaContent = new StringBuilder();
            Certificate[] chain = this.card.getKeyStore().getCertificateChain(alias);
            if (chain.length > 0) {
               X509Certificate firstInChain = (X509Certificate)chain[0];
               String dn = firstInChain.getSubjectX500Principal().toString();

               try {
                  LdapName ldapDN = new LdapName(dn);
                  Iterator var8 = ldapDN.getRdns().iterator();

                  while(var8.hasNext()) {
                     Rdn rdn = (Rdn)var8.next();
                     if (rdn.getType().equals("CN")) {
                        String cn = rdn.getValue().toString();
                        this.personalId = SmartCardLogic.findPersonalId(this.card.getKeyStore(), alias);
                        if (cn != null) {
                           String[] cnsplit = cn.split(" ");
                           this.firstName = cnsplit[0];
                           if (cnsplit.length > 1) {
                              this.lastName = cnsplit[1];
                           }
                        }
                     }
                  }
               } catch (InvalidNameException var12) {
                  Utils.logMessage("Error " + var12);
               }

               textAreaContent.append(this.firstName).append(" ").append(this.lastName).append(" [").append(this.personalId).append("]");
               text.append(textAreaContent.toString());
            }
         } else {
            text.append("Нема података о кориснику");
         }
      } catch (KeyStoreException var13) {
         Utils.logMessage("Error " + var13);
      }

      return text.toString();
   }

   private boolean checkCert(String baseUrl, KeyStore keyStore) {
      boolean ret = true;

      Certificate[] chain;
      try {
         label83: {
            String validAlias = SmartCardLogic.findAlias(this.card.getKeyStore());
            if (validAlias == null) {
               this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CERT_DATA + " #2");
               return false;
            }

            SigningLogic sl = new SigningLogic();
            sl.setChosenAlias(this.chosenAlias);
            sl.setXml(this.xml);
            if (this.xml == null) {
               sl.setXml(baseUrl + "/checkXML.jsp");
            }

            sl.setCard(this.card);
            sl.setFirstName(this.firstName);
            sl.setLastName(this.lastName);
            sl.setPersonalId(this.personalId);
            boolean success = sl.signXmlForLogin(keyStore);
            this.signatureStr = "";
            if (success) {
               this.signatureStr = sl.getSignatureStr();
               Utils.logMessage("TEST SIG: " + this.signatureStr);
               chain = this.card.getKeyStore().getCertificateChain(validAlias);
               if (chain.length > 0) {
                  X509Certificate firstInChain = (X509Certificate)chain[0];
                  firstInChain.checkValidity();
                  this.certStatusLabel.setText("<html>Валидан сертификат.</html>");
                  ret = true;
                  this.chosenAlias = validAlias;
                  break label83;
               }

               this.certStatusLabel.setText(SmartBox.NOTIFICATION_INVALID_CERT + "#1");
               return false;
            }

            this.certStatusLabel.setText(SmartBox.NOTIFICATION_INVALID_CERT + "#2");
            return false;
         }
      } catch (KeyStoreException var12) {
         Utils.logMessage("Error " + var12);
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_INVALID_CERT + "#3");
         return false;
      } catch (CertificateExpiredException var13) {
         Utils.logMessage("Error " + var13);
         this.certStatusLabel.setText("<html>Вашем сертификату је истекао период важења. Обратите се вашем сертификационом телу ради обнављања.</html>");
         return false;
      } catch (CertificateNotYetValidException var14) {
         Utils.logMessage("Error " + var14);
         this.certStatusLabel.setText("<html>Ваш сертификат још увек није валидиран. Обратите се вашем сертификационом телу за помоћ.</html>");
         return false;
      } catch (IOException var15) {
         Utils.logMessage("Error " + var15);
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_SERVER_ERROR);
         return false;
      }

      boolean ku = false;
      Certificate[] var17 = chain;
      int var18 = chain.length;

      for(int var19 = 0; var19 < var18; ++var19) {
         Certificate chain1 = var17[var19];
         X509Certificate chainMember = (X509Certificate)chain1;
         boolean[] keyUsage = chainMember.getKeyUsage();
         ku = ku || keyUsage[0] || keyUsage[1];
      }

      if (!ku) {
         this.certStatusLabel.setText("<html>Сертификат није намењен за непорецивост информација,<br>те стога не може бити коришћен на овом порталу.</html>");
      }

      return ret && ku;
   }

   private KeyStore loadKeyStore() {
      KeyStore keyStore = null;

      try {
         keyStore = this.card.loadKeyStore((char[])null);
      } catch (IOException var4) {
         Utils.logMessage("Error " + var4);
         String errorMsg = SmartBox.NOTIFICATION_CARD_BLOCKED;
         if (var4.getCause() != null && var4.getCause().getCause() != null) {
            if ("CKR_PIN_INCORRECT".equals(var4.getCause().getCause().getLocalizedMessage())) {
               errorMsg = SmartBox.NOTIFICATION_WRONG_PIN;
            } else if ("CKR_PIN_LOCKED".equals(var4.getCause().getCause().getLocalizedMessage())) {
               errorMsg = SmartBox.NOTIFICATION_CARD_BLOCKED;
            }
         }

         this.certStatusLabel.setText("<html>" + errorMsg + "</html>");
      } catch (CertificateException | KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException var5) {
         Utils.logMessage("Error " + var5);
         this.certStatusLabel.setText(SmartBox.NOTIFICATION_CARD_BROKEN);
         return null;
      }

      if (keyStore == null) {
         this.certStatusLabel.setText("<html>Грешка у одабиру<br>сертификационог тела<br>или његовом софтверу.</html>");
         return null;
      } else {
         String alias = SmartCardLogic.findAlias(keyStore);

         if (alias == null) {
            this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CERT_DATA + " #3");
            return null;
         } else {
            this.card.setKeyStore(keyStore);
            return keyStore;
         }
      }
   }

   private String generateLoginKey() {
      SecureRandom random = new SecureRandom();
      String timestamp = Long.toString(System.currentTimeMillis());
      String salt = (new BigInteger(130, random)).toString(32);
      return timestamp + salt;
   }

   private void enableLoginButtons() {
      switch(this.environment) {
      case ITO:
      case ETO:
         this.enableTestLoginButton();
         break;
      case PRODUCTION:
         this.enableProductionLoginButton();
         break;
      default:
         this.enableProductionLoginButton();
         this.enableTestLoginButton();
      }

   }

   private void enableProductionLoginButton() {
      this.loginProductionButton.setEnabled(true);
      this.loginProductionButton.setBackground(new Color(210, 44, 52));
   }

   private void enableTestLoginButton() {
      this.loginTestButton.setEnabled(true);
      this.loginTestButton.setBackground(new Color(33, 48, 84));
   }

   private void disableLoginButtons() {
      this.loginProductionButton.setEnabled(false);
      this.loginTestButton.setEnabled(false);
      this.loginProductionButton.setBackground(new Color(214, 215, 216));
      this.loginTestButton.setBackground(new Color(214, 215, 216));
   }

   private void startLoadingAnimation() {
      this.loadingAnimation.setIcon(new ImageIcon(this.getClass().getResource("/com/itsinbox/smartbox/resources/loading.gif")));
      this.pack();
   }

   private void stopLoadingAnimation() {
      this.loadingAnimation.setIcon((Icon)null);
      this.pack();
   }

   private void updateCheck() {
      (new Thread() {
         public void run() {
            try {
               URL url = new URL(SmartBox.VERSION_CHECK_URL);
               InputStream in = url.openStream();
               Reader reader = new InputStreamReader(in, "UTF-8");
               Properties prop = new Properties();
               prop.load(reader);
               reader.close();
               String newVersion = prop.getProperty("version");
               Utils.logMessage("Server version: " + newVersion);
               Utils.logMessage("Application version: " + SmartBox.VERSION);
               if (!newVersion.equals(SmartBox.VERSION)) {
                  LoginFrame.this.newAppVersion = newVersion;
                  LoginFrame.this.versionInfoLabel.setVisible(true);
                  LoginFrame.this.pack();
               }
            } catch (Exception var6) {
               Utils.logMessage("Error while checking for updates: " + var6.getMessage());
            }

         }
      }).start();
   }

   public void inserted(SmartCard card) {
      this.card = card;
      this.enableLoginButtons();
      this.certStatusLabel.setText(SmartBox.NOTIFICATION_STATUS_READY);
   }

   public void removed() {
      this.card = null;
      this.disableLoginButtons();
      this.certStatusLabel.setText(SmartBox.NOTIFICATION_NO_CARD);
   }

   public void actionPerformed(ActionEvent e) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
