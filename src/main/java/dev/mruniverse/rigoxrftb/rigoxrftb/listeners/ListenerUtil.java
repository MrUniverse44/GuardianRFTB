package dev.mruniverse.rigoxrftb.rigoxrftb.listeners;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;

public class ListenerUtil {
    public ListenerUtil(RigoxRFTB main){
        main.getServer().getPluginManager().registerEvents(new PlayerListeners(main),main);
    }
}
