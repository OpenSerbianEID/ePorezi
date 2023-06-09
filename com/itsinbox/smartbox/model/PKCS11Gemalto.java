package com.itsinbox.smartbox.model;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class PKCS11Gemalto extends PKCS11Card {

    public static final String[] KNOWN_ATRS = new String[]{
            "3B:7F:96:00:00:80:31:80:65:B0:85:59:56:FB:12:02:C1:82:90:00"
    };

    protected String getPKCS11ModuleName() {
        return "HalcomGemalto";
    }

    protected String getPKCS11ModulePath(int osFamily) {
        switch (osFamily) {
            case 2:
                return this.searchModulePaths(new String[]{"/usr/lib/libtokenapi.so"});
            case 4:
                ArrayList<String> paths = new ArrayList<>();
                RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
                List<String> arguments = runtimeMxBean.getInputArguments();
                for (String argument : arguments) {
                    if(argument.contains("libtokenapi.dylib"))
                    {
                        paths.add(argument.substring(argument.indexOf('/')));
                    }
                }
                paths.add("/Applications/Personal.app/Contents/Frameworks/libtokenapi.dylib");
                paths.add("/usr/local/lib/libtokenapi.dylib");
                paths.add("/usr/lib/libtokenapi.dylib");
                return this.searchModulePaths(paths.toArray(new String[0]));
            default:
                return null;
        }
    }

    protected String getPKCS11SlotIndex() {
        return "1";
    }
}
