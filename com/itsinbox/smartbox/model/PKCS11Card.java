package com.itsinbox.smartbox.model;

import com.itsinbox.smartbox.logic.SmartCardLogic;
import com.itsinbox.smartbox.model.SmartCard;
import com.itsinbox.smartbox.utils.Utils;
import sun.security.pkcs11.SunPKCS11;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.KeyStore.Builder;
import java.security.KeyStore.CallbackHandlerProtection;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public abstract class PKCS11Card extends SmartCard {

    private static final String KEYSTORE_TYPE = "PKCS11";
    private static final String KEYSTORE_PROVIDER = "SunPKCS11";

    public String getVendorName() {
        return "";
    }

    protected boolean moduleExists(String paths) {
        File file = new File(paths);
        return file.exists() && !file.isDirectory();
    }

    protected String searchModulePaths(String[] paths) {
        for (String path : paths) {
            Utils.logMessage("Trying PKCS11 module path " + path);
            if(this.moduleExists(path)) {
                return path;
            }
        }
        return null;
    }

    protected abstract String getPKCS11ModuleName();

    protected abstract String getPKCS11ModulePath(int osFamily);

    public KeyStore loadKeyStore(char[] pim) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
        int osFamily = getOsFamily();

        if(osFamily == 1) {
            String message = "Platform should not use PKCS11 module but MS CAPI instead";
            Utils.logMessage(message);
            throw new KeyStoreException(message);
        } else {
            String modulePath = this.getPKCS11ModulePath(osFamily);
            if(modulePath == null) {
                String message = "PKCS11 module not found!";
                Utils.logMessage(message);
                throw new KeyStoreException(message);
            } else {
                String moduleName = this.getPKCS11ModuleName();
                String moduleData = "name=" + moduleName + "\nlibrary=" + modulePath + "\nslotListIndex=1";
                Utils.logMessage("Loading PKCS11 module: " + moduleData);
                Provider provider = new SunPKCS11(new ByteArrayInputStream(moduleData.getBytes()));

                Utils.logMessage("Provider information:");
                Utils.logMessage("  Name: " + provider.getName());
                Utils.logMessage("  Version: " + provider.getVersion());
                Utils.logMessage("  Info: " + provider.getInfo());

                CallbackHandlerProtection callbackHandlerProtection = new CallbackHandlerProtection(new PKCS11Card.PinCallbackHandler());
                Builder builder = Builder.newInstance("PKCS11", (Provider) null, callbackHandlerProtection);
                Security.addProvider(provider);

//                Set<Provider.Service> services = provider.getServices();
//                for (Provider.Service service : services) {
//                    Utils.logMessage("Service: " + service.getType() + ", Algorithm: " + service.getAlgorithm());
//                }

                KeyStore keyStore = builder.getKeyStore();

//                Utils.logMessage("Printing aliases:");
//                Enumeration<String> aliases = keyStore.aliases();
//                while (aliases.hasMoreElements()) {
//                    String alias = aliases.nextElement();
//                    Utils.logMessage("  Alias: " + alias);
//                }

                SmartCardLogic._fixAliases(keyStore);
                Utils.logMessage("Number of entries in the key store: " + keyStore.size());

                return keyStore;
            }
        }
    }

//    public KeyStore loadKeyStore(char[] pim) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
//        int osFamily = getOsFamily();
//
//        if(osFamily == 1) {
//            String message = "Platform should not use PKCS11 module but MS CAPI instead";
//            Utils.logMessage(message);
//            throw new KeyStoreException(message);
//        } else {
//            String modulePath = this.getPKCS11ModulePath(osFamily);
//            if(modulePath == null) {
//                String message = "PKCS11 module not found!";
//                Utils.logMessage(message);
//                throw new KeyStoreException(message);
//            } else {
//                String moduleName = this.getPKCS11ModuleName();
//                String moduleData = "name=" + moduleName + "\nlibrary=" + modulePath + "\nslotListIndex=1";
//                Utils.logMessage("Loading PKCS11 module: " + moduleData);
//                Provider provider = new SunPKCS11(new ByteArrayInputStream(moduleData.getBytes()));
//
//                Security.addProvider(provider);
//                Builder builder = Builder.newInstance("PKCS11", null, new KeyStore.CallbackHandlerProtection(new PKCS11Card.PinCallbackHandler()));
//
//                Utils.logMessage("Provider information:");
//                Utils.logMessage("  Name: " + provider.getName());
//                Utils.logMessage("  Version: " + provider.getVersion());
//                Utils.logMessage("  Info: " + provider.getInfo());
//
//                Set<Provider.Service> services = provider.getServices();
//                for (Provider.Service service : services) {
//                    Utils.logMessage("Service: " + service.getType() + ", Algorithm: " + service.getAlgorithm());
//                }
//
//                KeyStore keyStore = builder.getKeyStore();
//                keyStore.load(null, "369183".toCharArray());
//
//                Utils.logMessage("Printing aliases:");
//                Enumeration<String> aliases = keyStore.aliases();
//                while (aliases.hasMoreElements()) {
//                    String alias = aliases.nextElement();
//                    Utils.logMessage("  Alias: " + alias);
//                }
//
//                SmartCardLogic._fixAliases(keyStore);
//                Utils.logMessage("Number of entries in the key store: " + keyStore.size());
//
//                return keyStore;
//            }
//        }
//    }

    public void sendAtr(String vendorName, String issuerCn) {}

    public String getKeyStoreProvider() {
        return "SunPKCS11";
    }

    public String getKeyStoreType() {
        return "PKCS11";
    }

    private static class PinCallbackHandler implements CallbackHandler {

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for(Callback callback : callbacks) {
                if(callback instanceof PasswordCallback) {
                    Utils.logMessage("PIN callback handler invoked");
                    PasswordCallback passwordCallback = (PasswordCallback)callback;
                    JPasswordField passwordField = new JPasswordField();
                    int dialogResult = JOptionPane.showConfirmDialog((Component)null, passwordField, "PIN", 2, -1);
                    if(dialogResult == 0) {
                        Utils.logMessage("PIN entry confirmed");
                        String password = new String(passwordField.getPassword());
                        passwordCallback.setPassword(password.toCharArray());
                    } else {
                        Utils.logMessage("PIN entry cancelled");
                    }
                    break;
                }
            }
        }

    }
}