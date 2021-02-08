package dev.mruniverse.rigoxrftb.core.games;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    CLASSIC;

    public String getType() {
        switch (this) {
            case DOUBLE_BEAST:
                return RigoxRFTB.getInstance().getFiles().getControl(RigoxFiles.SETTINGS).getString("gameType.DOUBLE_BEAST");
            case INFECTED:
                return RigoxRFTB.getInstance().getFiles().getControl(RigoxFiles.SETTINGS).getString("gameType.INFECTED");
            default:
                return RigoxRFTB.getInstance().getFiles().getControl(RigoxFiles.SETTINGS).getString("gameType.CLASSIC");
        }
    }
}
