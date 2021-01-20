package com.itsinbox.smartbox.model;

public class PKCS11SafeSign extends PKCS11Card {

    public static final String[] KNOWN_ATRS = new String[]{
            "3B:FA:18:00:FF:81:31:FE:45:4A:43:4F:50:32:31:56:32:33:31:65",
            "3B:7B:18:00:00:00:31:C0:64:77:E3:03:00:82:90:00",
            "3B:7D:96:00:00:80:31:80:65:B0:83:11:D0:A9:83:00:90:00",
            "3B:BB:18:00:C0:10:31:FE:45:80:67:04:12:B0:03:03:00:00:81:05:3C"
    };

    protected String getPKCS11ModuleName() {
        return "AETSafeSign";
    }

    protected String getPKCS11ModulePath(int osFamily) {
        switch (osFamily) {
            case 2:
                return this.searchModulePaths(new String[]{"/usr/lib/libaetpkss.so.3"});
            case 4:
                return this.searchModulePaths(new String[]{
                        "/Applications/tokenadmin.app/Contents/Frameworks/libaetpkss.dylib",
                        "/usr/local/lib/libaetpkss.dylib",
                        "/usr/lib/libaetpkss.dylib"
                });
            default:
                return null;
        }
    }

}
