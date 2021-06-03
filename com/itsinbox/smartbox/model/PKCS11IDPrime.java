package com.itsinbox.smartbox.model;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class PKCS11IDPrime extends PKCS11Card {

    public static final String[] KNOWN_ATRS = new String[]{
            "3B:7F:96:00:00:80:31:80:65:B0:85:59:56:FB:12:0F:FE:82:90:00",
    };

    protected String getPKCS11ModuleName() {
        return "IDPrime";
    }

    protected String getPKCS11ModulePath(int osFamily) {
        switch (osFamily){
            case 2:
                return this.searchModulePaths(new String[]{"/usr/lib/libidprimepkcs11.0.so"});
            case 4:
                ArrayList<String> paths = new ArrayList<>();
                RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
                List<String> arguments = runtimeMxBean.getInputArguments();
                for (String argument : arguments) {
                    if(argument.contains("libidprimepkcs11.0.dylib"))
                    {
                        paths.add(argument.substring(argument.indexOf('/')));
                    }
                }
                paths.add("/usr/local/lib/libidprimepkcs11.0.dylib");
                return this.searchModulePaths(paths.toArray(new String[0]));
            default:
                return null;
        }
    }
}