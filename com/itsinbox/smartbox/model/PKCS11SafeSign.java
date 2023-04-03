package com.itsinbox.smartbox.model;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class PKCS11SafeSign extends PKCS11Card {

    public static final String[] KNOWN_ATRS = new String[]{
            "3B:FA:18:00:FF:81:31:FE:45:4A:43:4F:50:32:31:56:32:33:31:65",
            "3B:7B:18:00:00:00:31:C0:64:77:E3:03:00:82:90:00",
            "3B:7D:96:00:00:80:31:80:65:B0:83:11:D0:A9:83:00:90:00",
            "3B:BB:18:00:C0:10:31:FE:45:80:67:04:12:B0:03:03:00:00:81:05:3C",
            "3B:DB:18:FF:81:91:FE:1F:C3:06:09:2B:06:01:04:01:E9:10:05:03:D7"
    };

    protected String getPKCS11ModuleName() {
        return "AETSafeSign";
    }

    protected String getPKCS11ModulePath(int osFamily) {
        switch (osFamily) {
            case 2:
                return this.searchModulePaths(new String[]{"/usr/lib/libaetpkss.so.3"});
            case 4:
                ArrayList<String> paths = new ArrayList<>();
                RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
                List<String> arguments = runtimeMxBean.getInputArguments();
                for (String argument : arguments) {
                    if(argument.contains("libaetpkss.dylib"))
                    {
                        paths.add(argument.substring(argument.indexOf('/')));
                    }
                }
                paths.add("/Applications/tokenadmin.app/Contents/Frameworks/libaetpkss.dylib");
                paths.add("/usr/local/lib/libaetpkss.dylib");
                paths.add("/usr/lib/libaetpkss.dylib");
                return this.searchModulePaths(paths.toArray(new String[0]));
            default:
                return null;
        }
    }

}
