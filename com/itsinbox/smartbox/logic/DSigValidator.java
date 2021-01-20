package com.itsinbox.smartbox.logic;

import com.itsinbox.smartbox.utils.Utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Provider;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DSigValidator {
   public void validate() {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);

      try {
         Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("C:\\Users\\schmee\\Downloads\\Primer_PPPDV_XML_Prilozi.xml"));
         NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
         if (nl.getLength() == 0) {
            return;
         }

         String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
         XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", (Provider)Class.forName(providerName).newInstance());
         DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));
         XMLSignature signature = fac.unmarshalXMLSignature(valContext);
         boolean coreValidity = signature.validate(valContext);
         if (!coreValidity) {
            Utils.logMessage("Error while validating signature!");
         } else {
            Utils.logMessage("Signature passed");
         }
      } catch (SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | MarshalException | XMLSignatureException | ParserConfigurationException var9) {
         Utils.logMessage("Error while validating signature: " + var9.getMessage());
      }

   }
}
