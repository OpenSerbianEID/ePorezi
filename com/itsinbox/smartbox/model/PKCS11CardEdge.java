package com.itsinbox.smartbox.model;

public class PKCS11CardEdge extends PKCS11Card {

    public static final String[] KNOWN_ATRS = new String[]{
            "3B:FF:94:00:00:81:31:80:43:80:31:80:65:B0:85:02:01:F3:12:0F:FF:82:90:00:79",
            "3B:F8:13:00:00:81:31:FE:45:4A:43:4F:50:76:32:34:31:B7",
            "3B:FA:13:00:00:81:31:FE:45:4A:43:4F:50:32:31:56:32:33:31:91",
            "3B:7D:94:00:00:80:31:80:65:B0:83:11:C0:A9:83:00:90:00",
            "3B:7D:94:00:00:80:31:80:65:B0:83:11:00:C8:83:00"
    };

    protected String getPKCS11ModuleName() {
        return "CardEdge";
    }

    protected String getPKCS11ModulePath(int osFamily) {
        switch (osFamily){
            case 2:
                return this.searchModulePaths(new String[]{"/usr/lib/libnstpkcs11.so"});
            case 4:
                return this.searchModulePaths(new String[]{"/usr/local/lib/libnstpkcs11.dylib"});
            default:
                return null;
        }
    }
}