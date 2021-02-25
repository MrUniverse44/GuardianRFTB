package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    CLASSIC;

    public String getType() {
        switch (this) {
            case DOUBLE_BEAST:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("gameType.DOUBLE_BEAST");
            case INFECTED:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("gameType.INFECTED");
            default:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("gameType.CLASSIC");
        }
    }
}
